package org.imdc.extensions.common

import com.inductiveautomation.ignition.common.BundleUtil
import com.inductiveautomation.ignition.common.Dataset
import com.inductiveautomation.ignition.common.PyUtilities
import org.python.core.Py
import org.python.core.PyObject
import java.lang.reflect.Method
import kotlin.streams.asSequence

class PyObjectAppendable(target: PyObject) : Appendable {
    private val writeMethod = target.__getattr__("write")

    override fun append(csq: CharSequence): Appendable = this.apply {
        writeMethod.__call__(Py.newStringOrUnicode(csq.toString()))
    }

    override fun append(csq: CharSequence, start: Int, end: Int): Appendable = this.apply {
        append(csq.subSequence(start, end))
    }

    override fun append(c: Char): Appendable = this.apply {
        append(c.toString())
    }
}

val Dataset.rowIndices: IntRange
    get() = (0 until rowCount)

val Dataset.columnIndices: IntRange
    get() = (0 until columnCount)

operator fun Dataset.get(row: Int, col: Int): Any? {
    return getValueAt(row, col)
}

inline fun <reified T> PyObject.toJava(): T {
    try {
        val cast =
            this.__tojava__(T::class.java) ?: throw Py.TypeError("Expected ${T::class.java.simpleName}, got None")
        return cast as T
    } catch (e: ClassCastException) {
        throw Py.TypeError("Expected ${T::class.java.simpleName}")
    }
}

inline fun <reified T> BundleUtil.addPropertyBundle() {
    addBundle(
        T::class.java.simpleName,
        T::class.java.classLoader,
        T::class.java.name.replace('.', '/'),
    )
}

inline fun <reified T : Annotation> Method.getAnnotation(): T? {
    return getAnnotation(T::class.java)
}

val PyObject.isSequence
    get() = PyUtilities.isSequence(this)
val PyObject.isString
    get() = PyUtilities.isString(this)

fun PyObject.asSequence(): Sequence<PyObject> = PyUtilities.stream(this).asSequence()
fun PyObject.asEntriesSequence(): Sequence<Pair<PyObject, PyObject>> =
    PyUtilities.streamEntries(this).map { (key, value) -> key to value }.asSequence()
