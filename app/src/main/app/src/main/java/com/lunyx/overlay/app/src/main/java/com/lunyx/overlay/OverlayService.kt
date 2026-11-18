package com.lunyx.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager

class OverlayService : Service(), SurfaceHolder.Callback {
    private lateinit var windowManager: WindowManager
    private lateinit var surfaceView: SurfaceView
    private lateinit var params: WindowManager.LayoutParams

    companion object {
        init { System.loadLibrary("lunyxoverlay") }
    }

    external fun nativeInit(surface: Surface, width: Int, height: Int): Boolean
    external fun nativeStartLoop()
    external fun nativeShutdown()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }

        surfaceView = SurfaceView(this).apply {
            holder.setFormat(PixelFormat.TRANSLUCENT)
            holder.addCallback(this@OverlayService)
        }
        windowManager.addView(surfaceView, params)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (nativeInit(holder.surface, surfaceView.width, surfaceView.height)) {
            Thread { nativeStartLoop() }.start()
        }
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) { nativeShutdown() }
    override fun onDestroy() {
        super.onDestroy()
        if (::surfaceView.isInitialized) windowManager.removeView(surfaceView)
    }
}
