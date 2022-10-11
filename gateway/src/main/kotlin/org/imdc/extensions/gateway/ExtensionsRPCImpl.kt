package org.imdc.extensions.gateway

import com.inductiveautomation.ignition.gateway.model.GatewayContext
import com.inductiveautomation.perspective.gateway.api.PerspectiveContext
import org.imdc.extensions.common.IconRPC
import java.io.IOException
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.isWritable
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ExtensionsRPCImpl(context: GatewayContext) : IconRPC {
    private val iconsDir =
        context.systemManager.dataDir.toPath() / "modules" / "com.inductiveautomation.perspective" / "icons"

    private val perspectiveContext = PerspectiveContext.get(context)

    override fun getIconLibraries(): List<String> {
        return perspectiveContext.iconManager.libraries.keys.toList()
    }

    override fun getIconPack(name: String): String? {
        val iconPack = iconsDir / name

        return iconPack.takeIf { it.exists() && it.isRegularFile() }?.readText()
    }

    override fun updateIconPack(name: String, content: String) {
        val iconPack = iconsDir / name

        if (iconPack.exists() && iconPack.isRegularFile() && iconPack.isWritable()) {
            iconPack.writeText(content)
        } else {
            throw IOException("$iconPack doesn't exist or is not able to be updated")
        }
    }
}
