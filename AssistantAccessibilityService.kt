package com.example.phoneassistant

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.net.Uri

class AssistantAccessibilityService : AccessibilityService() {

    companion object {
        var instance: AssistantAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun executeSafeCommand(command: String) {
        val c = command.lowercase()

        // Safe starter actions. More actions can be added explicitly.
        when {
            c.contains("تلگرام") && (c.contains("باز") || c.contains("open")) -> {
                val intent = packageManager.getLaunchIntentForPackage("org.telegram.messenger")
                if (intent != null) startActivity(intent)
            }
            c.contains("کروم") && (c.contains("باز") || c.contains("open")) -> {
                val intent = packageManager.getLaunchIntentForPackage("com.android.chrome")
                if (intent != null) startActivity(intent)
            }
            c.contains("تنظیمات") && (c.contains("باز") || c.contains("open")) -> {
                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            }
            else -> {
                // For unsupported commands, do not guess or perform risky actions.
            }
        }
    }

    fun visibleText(): String {
        val root = rootInActiveWindow ?: return ""
        val out = StringBuilder()
        collectText(root, out)
        return out.toString()
    }

    private fun collectText(node: AccessibilityNodeInfo, out: StringBuilder) {
        node.text?.let { if (it.isNotBlank()) out.append(it).append("\n") }
        node.contentDescription?.let { if (it.isNotBlank()) out.append(it).append("\n") }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectText(it, out); it.recycle() }
        }
    }

    fun tapText(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val nodes = root.findAccessibilityNodeInfosByText(text)
        for (node in nodes) {
            if (node.isClickable && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return true
            }
            var p = node.parent
            while (p != null) {
                if (p.isClickable && p.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
                p = p.parent
            }
        }
        return false
    }

    fun tap(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 100)
        dispatchGesture(GestureDescription.Builder().addStroke(stroke).build(), null, null)
    }
}
