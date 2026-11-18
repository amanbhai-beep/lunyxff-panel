package com.lunyx.overlay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }
        val title = TextView(this).apply {
            text = "LUNYX // FF SHIZUKU PANEL"
            textSize = 18f
        }
        layout.addView(title)

        val shizukuBtn = Button(this).apply {
            text = "1. Request Shizuku Permission"
            setOnClickListener {
                if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                    Toast.makeText(this@MainActivity, "Shizuku not running!", Toast.LENGTH_SHORT).show()
                } else {
                    if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this@MainActivity, "Already granted!", Toast.LENGTH_SHORT).show()
                    } else {
                        Shizuku.requestPermission(0)
                    }
                }
            }
        }
        layout.addView(shizukuBtn)

        val startBtn = Button(this).apply {
            text = "2. Launch Overlay"
            setOnClickListener {
                startService(Intent(this@MainActivity, OverlayService::class.java))
                Toast.makeText(this@MainActivity, "Service Started", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(startBtn)
        setContentView(layout)
    }
}
