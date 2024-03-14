package com.margarin.clock_library

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.scale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

class AnalogClockView(context: Context?, private val attrs: AttributeSet?) : View(context, attrs) {

    private var size = MINIMAL_VIEW_SIZE
    private var scalingCoefficient = 0f

    private var calendar = Calendar.getInstance(Locale.getDefault())
    private var hours = 0
    private var minutes = 0
    private var seconds = 0

    private var showSecondsHand = true

    private var updatePeriodInMillis = 1000L

    init {
        updateTime()
        setupAttrs()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(MINIMAL_VIEW_SIZE, widthSize)
            else -> MINIMAL_VIEW_SIZE
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(MINIMAL_VIEW_SIZE, heightSize)
            else -> MINIMAL_VIEW_SIZE
        }
        size = min(width, height)

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {

        drawClockFace(canvas = canvas).apply {
            scalingCoefficient = this
        }

        drawClockHand(
            canvas = canvas,
            scalingCoefficient = scalingCoefficient,
            clockHandImageResId = R.drawable.hour_hand,
            angle = hours * DEGREES_IN_CIRCLE / HOURS_IN_AM_PM
        )
        drawClockHand(
            canvas = canvas,
            scalingCoefficient = scalingCoefficient,
            clockHandImageResId = R.drawable.minute_hand,
            angle = minutes * DEGREES_IN_CIRCLE / MINUTES_IN_HOUR
        )
        if (showSecondsHand) {
            drawClockHand(
                canvas = canvas,
                scalingCoefficient = scalingCoefficient,
                clockHandImageResId = R.drawable.second_hand,
                angle = seconds * DEGREES_IN_CIRCLE / SECONDS_IN_MINUTE + 0.5f  //0.5f компенсирует кривизну секундной стрелки
            )
        }
    }

    private fun drawClockFace(canvas: Canvas): Float {
        val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.clock_face)
        val scaledBitmap = defaultBitmap.scale(size, size, false)
        val scalingCoefficient = defaultBitmap.height.toFloat() / scaledBitmap.height.toFloat()
        canvas.drawBitmap(scaledBitmap, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
        return scalingCoefficient
    }

    private fun drawClockHand(
        canvas: Canvas,
        scalingCoefficient: Float,
        clockHandImageResId: Int,
        angle: Float
    ) {
        val defaultBitmap = BitmapFactory.decodeResource(resources, clockHandImageResId)
        val height = (defaultBitmap.height / scalingCoefficient)
        val width = (defaultBitmap.width / scalingCoefficient)
        val scaledBitmap = defaultBitmap.scale(width.roundToInt(), height.roundToInt(), false)
        val offsetX = (size / 2 - width / 2)
        val matrix = Matrix().apply {
            postTranslate(offsetX, 0f)
            preRotate(angle, width / 2, height / 2)
        }
        canvas.drawBitmap(scaledBitmap, matrix, Paint(Paint.ANTI_ALIAS_FLAG))
    }

    private fun updateTime() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                updatePeriodInMillis = if (showSecondsHand) 1000L else 5000L
                calendar = Calendar.getInstance(Locale.getDefault())
                hours = calendar.get(Calendar.HOUR_OF_DAY)
                if (hours > HOURS_IN_AM_PM) hours -= HOURS_IN_AM_PM
                minutes = calendar.get(Calendar.MINUTE)
                seconds = calendar.get(Calendar.SECOND)
                invalidate()
                delay(updatePeriodInMillis)
            }
        }
    }

    private fun setupAttrs() {
        context?.obtainStyledAttributes(attrs, R.styleable.AnalogClockView, 0, 0).apply {
            showSecondsHand =
                this?.getBoolean(R.styleable.AnalogClockView_showSecondsHand, true) ?: true
            this?.recycle()
        }
    }

    fun setSecondsHandEnabled(isEnabled: Boolean) {
        showSecondsHand = isEnabled
    }

    companion object {

        private const val MINIMAL_VIEW_SIZE = 200

        private const val DEGREES_IN_CIRCLE = 360f

        private const val HOURS_IN_AM_PM = 12
        private const val MINUTES_IN_HOUR = 60
        private const val SECONDS_IN_MINUTE = 60
    }
}