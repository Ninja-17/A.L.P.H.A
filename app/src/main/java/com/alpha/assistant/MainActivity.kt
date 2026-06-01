package com.alpha.assistant

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.alpha.assistant.overlay.AlphaOverlayService

class MainActivity : ComponentActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnAccessibility: Button
    private lateinit var btnOverlay: Button
    private lateinit var btnNotifications: Button
    private lateinit var btnBattery: Button
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnAccessibility = findViewById(R.id.btnAccessibility)
        btnOverlay = findViewById(R.id.btnOverlay)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnBattery = findViewById(R.id.btnBattery)
        btnStart = findViewById(R.id.btnStart)

        btnAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        btnOverlay.setOnClickListener {
            openOverlaySettings()
        }

        btnNotifications.setOnClickListener {
            openNotificationSettings()
        }

        btnBattery.setOnClickListener {
            openBatterySettings()
        }

        btnStart.setOnClickListener {
            startServices()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val status = StringBuilder()

        status.appendLine("A.L.P.H.A. Estado:\n")

        val acc = isAccessibilityServiceEnabled()
        status.appendLine("${if (acc) "✅" else "❌"} Accesibilidad")

        val overlay = Settings.canDrawOverlays(this)
        status.appendLine("${if (overlay) "✅" else "❌"} Overlay")

        val notif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        } else true
        status.appendLine("${if (notif) "✅" else "❌"} Notificaciones")

        val battery = isBatteryOptimizationDisabled()
        status.appendLine("${if (battery) "✅" else "❌"} Sin optimización batería")

        tvStatus.text = status.toString()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = ComponentName(this, AlphaAccessibilityService::class.java)
        return try {
            getSystemService(Context.ACCESSIBILITY_SERVICE).let {
                val enabled = Settings.Secure.getInt(
                    contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED, 0
                )
                if (enabled == 1) {
                    val services = Settings.Secure.getString(
                        contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    ) ?: ""
                    services.contains(service.flattenToString())
                } else false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun isBatteryOptimizationDisabled(): Boolean {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun openOverlaySettings() {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
        )
    }

    private fun openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            )
        }
    }

    private fun openBatterySettings() {
        startActivity(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
        )
    }

    private fun startServices() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Primero activá el servicio de accesibilidad", Toast.LENGTH_LONG).show()
            openAccessibilitySettings()
            return
        }

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Primero activá el permiso de overlay", Toast.LENGTH_LONG).show()
            openOverlaySettings()
            return
        }

        ContextCompat.startForegroundService(
            this,
            Intent(this, AlphaForegroundService::class.java)
        )
        startService(Intent(this, AlphaOverlayService::class.java))
        Toast.makeText(this, "A.L.P.H.A. activado", Toast.LENGTH_SHORT).show()
        finish()
    }
}
