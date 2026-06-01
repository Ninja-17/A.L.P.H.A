package com.alpha.assistant.actions

import com.alpha.assistant.AlphaAccessibilityService

class ActionExecutor(private val service: AlphaAccessibilityService) {

    /**
     * Valida si el servicio de accesibilidad está listo para ejecutar comandos.
     */
    private fun isReady(): Boolean {
        return service.rootInActiveWindow != null
    }

    fun executeTap(x: Float, y: Float) {
        if (!isReady()) return
        service.tap(x, y)
    }

    fun executeSwipe(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long = 300) {
        if (!isReady()) return
        service.swipe(x1, y1, x2, y2, duration)
    }

    fun executeType(text: String) {
        if (!isReady()) return
        service.typeText(text)
    }

    fun executeGlobal(action: Int) {
        service.performGlobal(action)
    }
}
