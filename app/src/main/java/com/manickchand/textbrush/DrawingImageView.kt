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

    private val test_to_draw = "TEXT BRUSH"
    private var posX: Float = 0f
    private var posY: Float = 0f

    private var linePath: Path = Path()
    private var textPath: Path = Path()
    private var textCanvas: Canvas = Canvas()
    private var paint = Paint()

    private val positionsMap = ArrayList<Pair<Float, Float>>()
    private val undoList = ArrayList<Bitmap>()

    private var currentOriginalBitmap: Bitmap? = null

    fun builder() {
        textPath.reset()
        linePath.reset()
        val configBitmap = currentOriginalBitmap?.config ?: Bitmap.Config.ARGB_8888
        currentOriginalBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_island).copy( configBitmap, true)
        setImageBitmap(currentOriginalBitmap)
        configurePaint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //canvas?.rotate(-90f)
        canvas?.drawPath(linePath, paint)
        //canvas?.drawTextOnPath(test_to_draw, textPath, 0f,0f, paint)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val x = event?.x ?: 0f
        val y = event?.y ?: 0f
        positionsMap.add(Pair(x, y))
        Log.d("text", "onTouch: $x - $y")

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
            }

            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
            }

            MotionEvent.ACTION_UP -> {
                touchEnd(x, y)
            }
        }
        invalidate()
        return true
    }

    private fun configurePaint() {
        val scale = resources.displayMetrics.density
        paint.apply {
            color = Color.rgb(0, 0, 0)
            textSize = (16 * scale).roundToInt().toFloat()
        }
    }

    private fun touchStart(
        x: Float,
        y: Float
    ) {
        linePath.reset()
        posX = x
        posY = y
    }

    private fun touchMove(
        x: Float,
        y: Float
    ) {
        linePath.addCircle(x, y, 5f, Path.Direction.CW)
    }

    private fun touchEnd(
        x: Float,
        y: Float
    ) {
        drawable.toBitmapOrNull()?.let {
            undoList.add(it)
        }

        draw(x, y)
    }


    private fun draw(
        posX: Float,
        posY: Float,
    ) {
        linePath.reset()

        val bitmap =
            drawable.toBitmap().copy(currentOriginalBitmap!!.config, true)
                ?: throw Exception()

        textCanvas = Canvas(bitmap)
        textPath.lineTo(posX, posY)
        calculatePoints()
        textPath.reset()
        setImageBitmap(bitmap)
    }

    private fun calculatePoints(){

        val quantityChar = test_to_draw.length
        val q = positionsMap.size / quantityChar

        val list = positionsMap.filterIndexed {index, pair -> index % q == 0  }


        test_to_draw.forEachIndexed { index, c ->
            val p = list.getOrNull(index) ?: list.first()
            textCanvas.drawText(c.toString(), p.first, p.second, paint)
            //textPath.addCircle(it.first, it.second, 0f, Path.Direction.CW)
        }
    }

    fun undo(){
        val undoBitmap = undoList.removeLastOrNull()
        undoBitmap?.let {
            setImageBitmap(it)
        } ?: builder()
    }
}