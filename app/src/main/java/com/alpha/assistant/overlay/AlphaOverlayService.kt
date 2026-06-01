package com.alpha.assistant.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.alpha.assistant.R

class AlphaOverlayService : Service() {

    private lateinit var wm: WindowManager
    private var overlayView: View? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (overlayView == null) {
            createOverlay()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        overlayView?.let { wm.removeView(it) }
        overlayView = null
        super.onDestroy()
    }

    private fun createOverlay() {
        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_chat, null).apply {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 0
                y = 200
            }

            setOnTouchListener(object : View.OnTouchListener {
                private var initX = 0f
                private var initY = 0f
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                private var moved = false

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initX = params.x
                            initY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            moved = false
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = (initX + (event.rawX - initialTouchX)).toInt()
                            params.y = (initY + (event.rawY - initialTouchY)).toInt()
                            wm.updateViewLayout(this@apply, params)
                            moved = true
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (!moved) {
                                toggleExpand()
                            }
                            return true
                        }
                    }
                    return false
                }
            })

            wm.addView(this, params)
        }
    }

    private fun toggleExpand() {
        overlayView?.findViewById<TextView>(R.id.tvOverlayText)?.let {
            it.visibility = if (it.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    fun showMessage(message: String) {
        overlayView?.findViewById<TextView>(R.id.tvOverlayText)?.let {
            it.text = message
            it.visibility = View.VISIBLE
        }
    }
}
