package com.bolt.antigravity

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout

class EditorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val editText = EditText(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        gravity = Gravity.TOP or Gravity.START
        inputType = android.text.InputType.TYPE_CLASS_TEXT or
                   android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                   android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        typeface = android.graphics.Typeface.MONOSPACE
        textSize = 14f
        setTextColor(Color.WHITE)
        setPadding(40, 40, 40, 40)
        background = null // Minimalist
    }

    init {
        setBackgroundColor(Color.parseColor("#0A0A0A"))
        addView(editText)
    }

    fun setText(text: String) {
        editText.setText(text)
    }

    fun getText(): String = editText.text.toString()
}
