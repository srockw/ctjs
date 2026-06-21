package com.chattriggers.ctjs.api

import com.chattriggers.ctjs.CTJS
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier

object CustomKeyMapping {
    private val saveFile = CTJS.configLocation.resolve("ctjs_key_mappings.txt")
    private val keyMap: HashMap<String, String> = HashMap()
    private val categoryRegex = Regex("[a-zA-Z0-9-_\\s]+")
    private val customKeyMappings: MutableList<KeyMapping> = mutableListOf()
    private val customCategories: MutableMap<KeyMapping.Category, String> = mutableMapOf()

    @JvmStatic
    fun register(key: String, keyCode: Int, category: KeyMapping.Category): KeyMapping {
        val customKeyMapping = customKeyMappings.find { it.name == key }
        if (customKeyMapping != null) {
            return customKeyMapping.load()
        }

        val keyMapping = KeyMapping(key, InputConstants.Type.KEYSYM, keyCode, category).load()
        customKeyMappings.add(keyMapping)

        return keyMapping
    }

    @JvmStatic
    fun register(key: String, keyCode: Int, categoryName: String): KeyMapping {
        if (!categoryName.matches(categoryRegex)) {
            error("$categoryName should only contain alphanumeric characters, whitespaces, underscores or dashes")
        }

        val path = categoryName.lowercase().replace(Regex("\\s"), "_")
        val id = Identifier.fromNamespaceAndPath("ctjs", path)
        
        val category = customCategories.keys.find { it.id == id } ?: KeyMapping.Category(id)
        customCategories.putIfAbsent(category, categoryName)

        return register(key, keyCode, category)
    }

    @JvmStatic
    fun find(key: String): KeyMapping? {
        val vanilla = Minecraft.getInstance().options.keyMappings.find { it.name == key }
        if (vanilla != null) return vanilla

        return customKeyMappings.find { it.name == key }
    }

    @JvmStatic
    fun getKeyMappings() = customKeyMappings.toList()

    @JvmStatic
    fun getCategories() = customCategories.toMap()

    fun save() {
        val builder = StringBuilder()
        for (key in customKeyMappings) {
            builder.appendLine("${key.name}:${key.saveString()}")
        }
        saveFile.writeText(builder.toString())
    }

    fun load() {
        if (!saveFile.exists()) return

        saveFile.readText().lines().forEach { line ->
            val parts = line.split(":", limit = 2)
            if (parts.size < 2) return@forEach
            keyMap[parts[0]] = parts[1]
        }
    }

    private fun KeyMapping.load() = apply {
        keyMap[name]?.let {
            setKey(InputConstants.getKey(it))
            KeyMapping.resetMapping()
        }
    }
}
