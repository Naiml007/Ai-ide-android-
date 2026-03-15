package com.bolt.antigravity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin
import kotlin.random.Random

class PhysicsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val particles = List(20) { Particle() }
    private var startTime = System.currentTimeMillis()

    inner class Particle {
        var x = Random.nextFloat()
        var y = Random.nextFloat()
        var size = Random.nextFloat() * 10f + 5f
        var speed = Random.nextFloat() * 0.001f + 0.0005f
        var offset = Random.nextFloat() * 2 * Math.PI.toFloat()

        fun draw(canvas: Canvas, width: Int, height: Int, time: Long) {
            val currentY = (y - (time * speed) % 1.0f + 1.0f) % 1.0f
            val drift = sin(time * 0.002 + offset).toFloat() * 20f

            canvas.drawCircle(
                x * width + drift,
                currentY * height,
                size,
                paint
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val time = System.currentTimeMillis() - startTime

        particles.forEach { it.draw(canvas, width, height, time) }

        invalidate() // High-performance loop
    }
}
