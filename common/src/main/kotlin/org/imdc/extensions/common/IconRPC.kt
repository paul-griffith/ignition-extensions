package org.imdc.extensions.common

interface IconRPC {
    fun getIconLibraries(): List<String>
    fun getIconPack(name: String): String?
    fun updateIconPack(name: String, content: String)
}
