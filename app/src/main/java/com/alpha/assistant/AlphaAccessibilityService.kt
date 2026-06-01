package com.alpha.assistant

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.alpha.assistant.db.entity.EventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlphaAccessibilityService : AccessibilityService() {

    companion object {
        var instance: AlphaAccessibilityService? = null
            private set
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // Nueva lógica de debouncing
    private var lastProcessedEventTime = 0L
    private val actionExecutor = com.alpha.assistant.actions.ActionExecutor(this)

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessedEventTime < 2000) return // Debounce de 2s
        lastProcessedEventTime = currentTime

        val root = rootInActiveWindow ?: return

        scope.launch {
            val screenText = extractText(root)
            val app = event.packageName?.toString() ?: ""

            // Solo guardamos si realmente hay texto, para ahorrar DB
            if (screenText.isNotBlank()) {
                val entity = EventEntity(
                    packageName = app,
                    eventType = event.eventType,
                    screenText = screenText,
                    timestamp = System.currentTimeMillis()
                )
                AlphaApplication.instance.database.eventDao().insert(entity)
            }
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun tap(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
    }

    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long = 300) {
        val path = Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()
        dispatchGesture(gesture, null, null)
    }

    fun typeText(text: String) {
        val focused = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT) ?: return
        val previous = focused.text
        Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, this)
        }
    }

    fun performGlobal(action: Int): Boolean {
        return performGlobalAction(action)
    }

    private fun extractText(node: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        extractTextRecursive(node, sb)
        return sb.toString()
    }

    private fun extractTextRecursive(node: AccessibilityNodeInfo, sb: StringBuilder) {
        if (node.text != null) {
            sb.appendLine(node.text)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { extractTextRecursive(it, sb) }
        }
    }
}
