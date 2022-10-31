package org.imdc.extensions.common

import com.inductiveautomation.ignition.common.expressions.ConstantExpression
import com.inductiveautomation.ignition.common.expressions.Expression
import com.inductiveautomation.ignition.common.expressions.ExpressionParseContext
import com.inductiveautomation.ignition.common.expressions.FunctionFactory
import com.inductiveautomation.ignition.common.expressions.parsing.ELParserHarness
import com.inductiveautomation.ignition.common.model.CommonContext
import com.inductiveautomation.ignition.common.model.values.QualifiedValue
import com.inductiveautomation.ignition.common.script.PyArgParser
import com.inductiveautomation.ignition.common.script.ScriptContext
import com.inductiveautomation.ignition.common.script.builtin.KeywordArgs
import com.inductiveautomation.ignition.common.script.hints.ScriptFunction
import com.inductiveautomation.ignition.common.tags.paths.parser.TagPathParser
import org.python.core.Py
import org.python.core.PyDictionary
import org.python.core.PyException
import org.python.core.PyList
import org.python.core.PyObject
import java.util.concurrent.TimeUnit

class UtilitiesExtensions(
    @get:ScriptFunction(docBundlePrefix = "UtilitiesExtensions")
    @get:UnsafeExtension
    val context: CommonContext,
) {
    @ScriptFunction(docBundlePrefix = "UtilitiesExtensions")
    @KeywordArgs(names = ["object"], types = [PyObject::class])
    fun deepCopy(args: Array<PyObject?>?, keywords: Array<String?>?): PyObject {
        val parsedArgs = PyArgParser.parseArgs(args, keywords, this.javaClass, "deepCopy")
        val toConvert = parsedArgs.getPyObject("object")
            .orElseThrow { Py.TypeError("deepCopy requires one argument, got none") }
        return toConvert.toBuiltin()
    }

    @ScriptFunction(docBundlePrefix = "UtilitiesExtensions")
    @KeywordArgs(names = ["expression"], types = [String::class])
    fun evalExpression(args: Array<PyObject>, keywords: Array<String>): QualifiedValue {
        if (args.isEmpty()) {
            throw Py.ValueError("Must supply at least one argument to evalExpression")
        }
        val expression = args[0].toJava<String>()
        val keywordMap = keywords.indices.associate { i ->
            keywords[i] to args[i + 1].toJava<Any?>()
        }

        val parseContext = KeywordParseContext(keywordMap)

        return EXPRESSION_PARSER.parse(expression, parseContext).run {
            try {
                startup()
                execute()
            } finally {
                shutdown()
            }
        }
    }

    private inner class KeywordParseContext(val keywords: Map<String, Any?>) : ExpressionParseContext {
        override fun createBoundExpression(reference: String): Expression {
            require(reference.isNotEmpty()) { "Invalid path $reference" }
            return if (reference in keywords) {
                ConstantExpression(keywords[reference])
            } else {
                val path = TagPathParser.parse(ScriptContext.defaultTagProvider(), reference)
                val tagValues = context.tagManager.readAsync(listOf(path)).get(30, TimeUnit.SECONDS)
                ConstantExpression(tagValues[0].value)
            }
        }

        override fun getFunctionFactory(): FunctionFactory = context.expressionFunctionFactory
    }

    companion object {
        private val EXPRESSION_PARSER = ELParserHarness()

        private fun PyObject.toBuiltin(): PyObject = when {
            isMappingType -> asEntriesSequence()
                .map { (key, value) ->
                    key to value.toBuiltin()
                }
                .toMap(PyDictionary())

            isSequence -> asSequence()
                .mapTo(PyList()) {
                    it.toBuiltin()
                }

            isString -> this
            else -> try {
                val iterable = asIterable()
                PyList(iterable.iterator())
            } catch (pye: PyException) {
                this
            }
        }
    }
}
