package com.chattriggers.ctjs.api.triggers

import kotlin.math.max

class StepTrigger(method: Any) : Trigger(method, TriggerType.STEP) {
    private var delay: Long = 1000
    private var nextAction: Long = -1

    fun setDelay(seconds: Long) {
        delay = seconds * 1000
        nextAction = System.currentTimeMillis() + delay
    }

    fun setFps(fps: Long) {
        delay = 1000 / max(1, fps)
        nextAction = System.currentTimeMillis() + delay
    }

    override fun trigger(args: Array<out Any?>) {
        val now = System.currentTimeMillis()
        if (now > nextAction) {
            nextAction = now + delay
            callMethod(args)
        }
    }
}
