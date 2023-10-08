package com.manickchand.textbrush

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import kotlin.math.roundToInt

class DrawingImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(
    context,
    attrs,
    defStyleAttr
) {

    private val textToDraw = "TEXT BRUSH"
    private var startX: Float = 0f
    private var startY: Float = 0f

    private var linePath: Path = Path()
    private var textCanvas: Canvas = Canvas()
    private var paint = Paint()

    private val positionsMap = ArrayList<Pair<Float, Float>>()
    private val undoList = ArrayList<Bitmap>()

    private var currentOriginalBitmap: Bitmap? = null

    fun builder() {
        linePath.reset()
        val configBitmap = currentOriginalBitmap?.config ?: Bitmap.Config.ARGB_8888
        currentOriginalBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.img_to_draw).copy(configBitmap, true)
        setImageBitmap(currentOriginalBitmap)
        configurePaint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            textCanvas = it
            it.drawPath(linePath, paint)
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val x = event?.x ?: 0f
        val y = event?.y ?: 0f
        positionsMap.add(Pair(x, y))

        Log.d("text", "onTouch: $x - $y")

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                positionsMap.clear()
                linePath.reset()
                startX = x
                startY = y
            }

            MotionEvent.ACTION_MOVE -> {
                linePath.addCircle(x, y, 20f, Path.Direction.CW)
            }

            MotionEvent.ACTION_UP -> {
                drawable.toBitmapOrNull()?.let {
                    undoList.add(it)
                }

                drawText()
            }
        }
        invalidate()
        return true
    }

    private fun configurePaint() {
        val scale = resources.displayMetrics.density
        paint.apply {
            color = Color.RED
            textSize = (36 * scale).roundToInt().toFloat()
            textAlign = Paint.Align.LEFT
        }
    }

    private fun drawText() {
        linePath.reset()

        val bitmap =
            drawable.toBitmap().copy(currentOriginalBitmap!!.config, true)
                ?: throw Exception()

        textCanvas.setBitmap(bitmap)

        calculatePoints()
        setImageBitmap(bitmap)
    }

    private fun calculatePoints() {

        val quantityChar = textToDraw.length
        var interval = positionsMap.size / quantityChar
        if (interval < 1) interval = 1
        if (positionsMap.size < quantityChar) {
            textCanvas.drawText(textToDraw, startX, startY, paint)
        } else {

            val finalPoints = ArrayList<Pair<Float, Float>>()
            val listChunked = positionsMap.chunked(interval)

            listChunked.forEach {
                Log.d("text", "point $it")
                finalPoints.add(it.first())
            }

            textToDraw.forEachIndexed { index, c ->
                val p = finalPoints.getOrNull(index) ?: finalPoints.first()
                textCanvas.drawText(c.toString(), p.first, p.second, paint)
            }
        }
    }

    fun undo() {
        val undoBitmap = undoList.removeLastOrNull()
        undoBitmap?.let {
            setImageBitmap(it)
        } ?: builder()
    }
}