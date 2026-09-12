package com.example.phoneassistant

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import android.graphics.Color
import android.view.ViewGroup

class MainActivity : android.app.Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }

        val title = TextView(this).apply {
            text = "📱 Phone Assistant"
            textSize = 28f
        }
        box.addView(title)

        val info = TextView(this).apply {
            text = "دستور خودت را بنویس. برای کنترل برنامه‌های دیگر باید Accessibility را فعال کنی."
            textSize = 17f
            setPadding(0, 24, 0, 24)
        }
        box.addView(info)

        val command = EditText(this).apply {
            hint = "مثلاً: تلگرام را باز کن"
            minLines = 3
            gravity = 48
        }
        box.addView(command, LinearLayout.LayoutParams(-1, 0, 1f))

        val run = Button(this).apply { text = "▶ اجرای دستور" }
        box.addView(run)

        val settings = Button(this).apply { text = "⚙ فعال‌کردن دسترسی دستیار" }
        box.addView(settings)

        val status = TextView(this).apply {
            text = "وضعیت: آماده"
            textSize = 16f
            setPadding(0, 20, 0, 0)
        }
        box.addView(status)

        settings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        run.setOnClickListener {
            val text = command.text.toString().trim()
            if (text.isEmpty()) {
                status.text = "یک دستور وارد کن."
                return@setOnClickListener
            }
            val service = AssistantAccessibilityService.instance
            if (service == null) {
                status.text = "ابتدا Accessibility را فعال کن."
            } else {
                service.executeSafeCommand(text)
                status.text = "دستور دریافت شد: $text"
            }
        }

        setContentView(box)
    }
}
