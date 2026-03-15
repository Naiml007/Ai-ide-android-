package com.bolt.antigravity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView

class EditorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val lineNumbersView = LineNumbersView(context)
    private val editText = EditText(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        gravity = Gravity.TOP or Gravity.START
        inputType = android.text.InputType.TYPE_CLASS_TEXT or
                   android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                   android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        typeface = Typeface.MONOSPACE
        textSize = 14f
        setTextColor(Color.parseColor("#F8F8F2")) // Monokai White
        setPadding(20, 40, 40, 40)
        background = null
    }

    private val scrollView = ScrollView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val container = LinearLayout(context).apply {
            orientation = HORIZONTAL
            addView(lineNumbersView)
            addView(editText)
        }
        addView(container)
    }

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#272822")) // Monokai Background
        addView(scrollView)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lineNumbersView.updateLineCount(editText.lineCount)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun setText(text: String) {
        editText.setText(text)
    }

    private class LineNumbersView(context: Context) : android.view.View(context) {
        private var lineCount = 1
        private val paint = Paint().apply {
            color = Color.parseColor("#75715E") // Monokai Gray
            textSize = 30f
            typeface = Typeface.MONOSPACE
            textAlign = Paint.Align.RIGHT
        }

        init {
            layoutParams = LayoutParams(100, LayoutParams.MATCH_PARENT)
            setPadding(0, 40, 20, 0)
        }

        fun updateLineCount(count: Int) {
            if (lineCount != count) {
                lineCount = count
                invalidate()
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val lineHeight = 55f // Approx matching EditText
            for (i in 1..lineCount) {
                canvas.drawText(i.toString(), width.toFloat() - 10, i * lineHeight + 40, paint)
            }
        }
    }
}
