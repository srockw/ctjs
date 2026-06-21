package com.chattriggers.ctjs.internal.engine.module

import java.io.File

class Module(val name: String, var metadata: ModuleMetadata, val folder: File) {
    var requiredBy = mutableSetOf<String>()

    override fun toString() = "Module{name=$name,version=${metadata.version}}"
}
