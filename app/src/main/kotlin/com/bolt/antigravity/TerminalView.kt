package com.bolt.antigravity

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStreamWriter
import java.util.concurrent.Executors

class TerminalView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val outputTextView = TextView(context).apply {
        setTextColor(Color.GREEN)
        textSize = 12f
        typeface = android.graphics.Typeface.MONOSPACE
        setBackgroundColor(Color.parseColor("#1A1A1A"))
    }

    private val inputField = EditText(context).apply {
        setTextColor(Color.WHITE)
        textSize = 12f
        typeface = android.graphics.Typeface.MONOSPACE
        setBackgroundColor(Color.TRANSPARENT)
        hint = "> "
        setHintTextColor(Color.GRAY)
        maxLines = 1
        inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE
    }

    private val scrollView = ScrollView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
        addView(outputTextView)
    }

    private val executor = Executors.newSingleThreadExecutor()
    private var writer: BufferedWriter? = null

    // Bolt Optimization: Efficient Ring Buffer for Terminal lines
    private val maxLines = 150
    private val lineBuffer = mutableListOf<String>()

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#1A1A1A"))
        setPadding(16, 16, 16, 16)

        addView(scrollView)
        addView(inputField)

        setupTerminal()
        setupInput()
    }

    private fun setupTerminal() {
        executor.execute {
            try {
                // Try to find a working shell, fallback if necessary
                val shellPath = if (java.io.File("/system/bin/sh").exists()) "/system/bin/sh" else "sh"
                val process = ProcessBuilder(shellPath, "-i")
                    .redirectErrorStream(true)
                    .start()

                writer = BufferedWriter(OutputStreamWriter(process.outputStream))
                val inputStream = process.inputStream
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    val text = String(buffer, 0, bytesRead)
                    updateUI(text)
                }
            } catch (e: Exception) {
                updateUI("\nError: ${e.message}\n")
            }
        }
    }

    private fun setupInput() {
        inputField.setOnEditorActionListener { _, _, event ->
            val command = inputField.text.toString()
            if (command.isNotEmpty()) {
                sendCommand(command)
                inputField.setText("")
            }
            true
        }
    }

    private fun sendCommand(command: String) {
        executor.execute {
            try {
                writer?.write(command + "\n")
                writer?.flush()
            } catch (e: Exception) {
                updateUI("\nFailed to send: ${e.message}\n")
            }
        }
    }

    private fun updateUI(text: String) {
        post {
            // Efficient append: avoid re-splitting entire history
            lineBuffer.add(text)
            if (lineBuffer.size > maxLines) {
                lineBuffer.removeAt(0)
                // Bolt Optimization: Hard-prune TextView to prevent memory leak and lag
                val currentText = outputTextView.text
                if (currentText.length > 5000) {
                    outputTextView.text = currentText.substring(2000)
                }
            }

            // Rebuild only when necessary, or just append
            outputTextView.append(text)

            // Auto-scroll
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }
    }
}
