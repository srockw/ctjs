package com.chattriggers.ctjs.api

import com.chattriggers.ctjs.internal.listeners.ClientListener
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class CustomScreen(val name: String) : Screen(Component.literal(name)) {
    var onInit: () -> Unit = {}
    var onRender: (GuiGraphicsExtractor, Int, Int, Float) -> Unit = { _, _, _, _ -> }
    var onKeyPressed: (KeyEvent) -> Unit = {}
    var onMouseClicked: (MouseButtonEvent, Boolean) -> Unit = { _, _ -> }
    var onMouseScrolled: (Double, Double, Double, Double) -> Unit = { _, _, _, _ -> }
    var onMouseDragged: (MouseButtonEvent, Double, Double) -> Unit = { _, _, _ -> }
    var onScreenClose: () -> Unit = {}

    fun open() {
        ClientListener.addTask(0) {
            Minecraft.getInstance().setScreen(this)
        }
    }

    fun <T> addToWidgets(widget: T) where T : GuiEventListener, T : NarratableEntry = addWidget(widget)

    fun <T> addToRenderableWidgets(widget: T) where T : GuiEventListener, T : NarratableEntry, T : Renderable =
        addRenderableWidget(widget)

    override fun init() {
        onInit()
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        onRender(graphics, mouseX, mouseY, a)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val ret = super.mouseClicked(event, doubleClick)
        onMouseClicked(event, doubleClick)
        return ret
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        val ret = super.keyPressed(event)
        onKeyPressed(event)
        return ret
    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
        val ret = super.mouseScrolled(x, y, scrollX, scrollY)
        onMouseScrolled(x, y, scrollX, scrollY)
        return ret
    }

    override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
        val ret = super.mouseDragged(event, dx, dy)
        onMouseDragged(event, dx, dy)
        return ret
    }

    override fun onClose() {
        super.onClose()
        onScreenClose()
    }
}
