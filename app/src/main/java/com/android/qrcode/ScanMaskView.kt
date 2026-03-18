package com.android.qrcode


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ScanMaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val maskPaint = Paint().apply {
        color = Color.parseColor("#80000000")
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val cornerPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val scanLinePaint = Paint().apply {
        color = Color.parseColor("#82B7FF")
        strokeWidth = 5f
        isAntiAlias = true
    }

    private var scanRect = RectF()
    private var scanLineY = 0f

    private var animator: ValueAnimator? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val layer = canvas.saveLayer(null, null)

        // 1. 画遮罩
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)

        // 2. 挖空扫描框
        canvas.drawRect(scanRect, clearPaint)

        canvas.restoreToCount(layer)

        // 3. 边框
        canvas.drawRect(scanRect, borderPaint)

        // 4. 四个角
        val cornerLength = 40f

        // 左上
        canvas.drawLine(scanRect.left, scanRect.top, scanRect.left + cornerLength, scanRect.top, cornerPaint)
        canvas.drawLine(scanRect.left, scanRect.top, scanRect.left, scanRect.top + cornerLength, cornerPaint)

        // 右上
        canvas.drawLine(scanRect.right, scanRect.top, scanRect.right - cornerLength, scanRect.top, cornerPaint)
        canvas.drawLine(scanRect.right, scanRect.top, scanRect.right, scanRect.top + cornerLength, cornerPaint)

        // 左下
        canvas.drawLine(scanRect.left, scanRect.bottom, scanRect.left + cornerLength, scanRect.bottom, cornerPaint)
        canvas.drawLine(scanRect.left, scanRect.bottom, scanRect.left, scanRect.bottom - cornerLength, cornerPaint)

        // 右下
        canvas.drawLine(scanRect.right, scanRect.bottom, scanRect.right - cornerLength, scanRect.bottom, cornerPaint)
        canvas.drawLine(scanRect.right, scanRect.bottom, scanRect.right, scanRect.bottom - cornerLength, cornerPaint)

        // 5. 扫描线
        canvas.drawLine(scanRect.left, scanLineY, scanRect.right, scanLineY, scanLinePaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val size = w * 0.7f
        val left = (w - size) / 2
        val top = (h - size) / 3

        scanRect = RectF(left, top, left + size, top + size)

        startScanAnim()
    }

    private fun startScanAnim() {
        animator?.cancel()

        animator = ValueAnimator.ofFloat(scanRect.top, scanRect.bottom).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                scanLineY = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}