package com.bolt.antigravity

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
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
        setTextColor(Color.parseColor("#A6E22E")) // Monokai Green
        textSize = 12f
        typeface = Typeface.MONOSPACE
        setPadding(20, 20, 20, 20)
    }

    private val inputField = EditText(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setTextColor(Color.WHITE)
        textSize = 12f
        typeface = Typeface.MONOSPACE
        setBackgroundColor(Color.parseColor("#1A1A1A"))
        hint = "Enter command..."
        setHintTextColor(Color.DKGRAY)
        maxLines = 1
        inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        imeOptions = EditorInfo.IME_ACTION_DONE
        setPadding(20, 20, 20, 20)
    }

    private val scrollView = ScrollView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
        addView(outputTextView)
    }

    private val executor = Executors.newFixedThreadPool(2)
    private var writer: BufferedWriter? = null

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.BLACK)

        addView(scrollView)
        addView(inputField)

        setupTerminal()
        setupInput()
    }

    private fun setupTerminal() {
        executor.execute {
            try {
                val shellPath = if (java.io.File("/system/bin/sh").exists()) "/system/bin/sh" else "sh"
                val process = ProcessBuilder(shellPath, "-i")
                    .redirectErrorStream(true)
                    .start()

                writer = BufferedWriter(OutputStreamWriter(process.outputStream))
                val inputStream = process.inputStream

                // Bolt: High-performance reading loop
                val reader = inputStream.bufferedReader()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    updateUI(line!! + "\n")
                }
            } catch (e: Exception) {
                updateUI("Terminal Error: ${e.message}\n")
            }
        }
    }

    private fun setupInput() {
        inputField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                val command = inputField.text.toString()
                if (command.isNotEmpty()) {
                    updateUI("$ " + command + "\n")
                    sendCommand(command)
                    inputField.setText("")
                }
                true
            } else false
        }
    }

    private fun sendCommand(command: String) {
        executor.execute {
            try {
                writer?.write(command + "\n")
                writer?.flush()
            } catch (e: Exception) {
                updateUI("Failed to send: ${e.message}\n")
            }
        }
    }

    private fun updateUI(text: String) {
        post {
            outputTextView.append(text)

            // Limit buffer size to prevent memory bloat
            if (outputTextView.length() > 10000) {
                outputTextView.text = outputTextView.text.subSequence(5000, outputTextView.length())
            }

            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }
    }
}
