package com.chattriggers.ctjs.api

import com.chattriggers.ctjs.engine.LogType
import com.chattriggers.ctjs.engine.printToConsole
import com.chattriggers.ctjs.internal.mixins.CommandNodeAccessor
import com.chattriggers.ctjs.internal.utils.Initializer
import com.chattriggers.ctjs.internal.utils.onExecute
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.commands.CommandSource

object CustomCommand : Initializer {
    private var commands: MutableSet<Pair<String, (NodeBuilder) -> Any>> = mutableSetOf()
    private var clientDispatcher: CommandDispatcher<CommandSource>? = null
    private var networkDispatcher: CommandDispatcher<CommandSource>? = null

    @Suppress("UNCHECKED_CAST")
    override fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            this.clientDispatcher = dispatcher as CommandDispatcher<CommandSource>
            registerAll(dispatcher)
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            clientDispatcher = null
            networkDispatcher = null
        }
    }

    internal fun registerNetwork(dispatcher: CommandDispatcher<CommandSource>) {
        networkDispatcher = dispatcher
        registerAll(dispatcher)
    }

    internal fun registerAll(dispatcher: CommandDispatcher<CommandSource>) {
        for ((name, callback) in commands) {
            dispatcher.register(buildCommand(name, callback))
        }
    }

    internal fun unregisterAll() {
        for (dispatcher in listOfNotNull(clientDispatcher, networkDispatcher)) {
            for ((name, _) in commands) {
                (dispatcher.root as CommandNodeAccessor).let {
                    it.children.remove(name)
                    it.literals.remove(name)
                }
            }
        }

        commands.clear()
    }

    @JvmStatic
    fun register(name: String, callback: (NodeBuilder) -> Any) {
        commands.add(name to callback)

        if (clientDispatcher?.root?.getChild(name) != null || networkDispatcher?.root?.getChild(name) != null) {
            "Command with $name already exists".printToConsole(LogType.WARN)
        } else {
            val cmd = buildCommand(name, callback)
            clientDispatcher?.register(cmd)
            networkDispatcher?.register(cmd)
        }
    }

    private fun buildCommand(name: String, callback: (NodeBuilder) -> Any) =
        LiteralArgumentBuilder.literal<CommandSource>(name).apply {
            callback(NodeBuilder(this))
        }

    class NodeBuilder(val node: ArgumentBuilder<CommandSource, *>) {
        fun literal(s: String, callback: (NodeBuilder) -> Any) = apply {
            val next = LiteralArgumentBuilder.literal<CommandSource>(s)
            callback(NodeBuilder(next))
            node.then(next)
        }

        fun <T> argument(name: String, type: ArgumentType<T>, callback: (NodeBuilder) -> Any) = apply {
            val next = RequiredArgumentBuilder.argument<CommandSource, T>(name, type)
            callback(NodeBuilder(next))
            node.then(next)
        }

        fun exec(callback: (CommandContext<CommandSource>) -> Any) = apply {
            node.onExecute { callback(it) }
        }
    }
}
