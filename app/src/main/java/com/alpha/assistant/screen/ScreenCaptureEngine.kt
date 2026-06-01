package com.alpha.assistant.screen

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.hardware.display.DisplayManager
import android.media.ImageReader

class ScreenCaptureEngine(private val context: Context) {

    private val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    fun startCapture(resultCode: Int, data: Intent, width: Int, height: Int) {
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "AlphaScreenCapture",
            width,
            height,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
    }

    fun stopCapture() {
        virtualDisplay?.release()
        mediaProjection?.stop()
    }
}
