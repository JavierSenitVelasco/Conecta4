package com.example.e339744.conecta4.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.activities.setColor
import com.example.e339744.conecta4.model.TableroConecta4
import es.uam.eps.multij.Tablero
import java.lang.Exception

class ViewConecta4(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val DEBUG = "View4EnRaya"
    private var numero: Int = 0
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint2 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint2 = Paint(Paint.ANTI_ALIAS_FLAG)
    private var heightOfTile: Float = 0.toFloat()
    private var widthOfTile: Float = 0.toFloat()
    private var radio: Float = 0.toFloat()
    private var filas: Int = 6
    private var columnas: Int = 7
    private var board: TableroConecta4? = null
    private var onPlayListener: OnPlayListener? = null

    interface OnPlayListener {
        fun onPlay(row: Int, column: Int)
    }

    init {
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = Color.parseColor("#fff693")

        backgroundPaint2.style = Paint.Style.STROKE
        backgroundPaint2.color = Color.BLACK
        backgroundPaint2.strokeWidth = 8f

        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = 2f

        linePaint2.style = Paint.Style.STROKE
        linePaint2.strokeWidth = 2f
        linePaint2.color = Color.BLACK

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int


        if (widthSize < heightSize) {
            heightSize = widthSize
            height = heightSize
            width = height
        } else {
            widthSize = heightSize
            height = widthSize
            width = height
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthOfTile = (w / columnas).toFloat()
        heightOfTile = (h / filas).toFloat()
        if (widthOfTile < heightOfTile) {
            radio = widthOfTile * 0.3f
        } else {
            radio = heightOfTile * 0.3f
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val boardWidth = width.toFloat()
        val boardHeight = height.toFloat()

        var rect = RectF(0f, 0f, boardWidth, boardHeight)
        canvas.drawRoundRect(rect, 25f, 25f, backgroundPaint)
        canvas.drawRoundRect(rect, 25f, 25f, backgroundPaint2)
        drawCircles(canvas, linePaint)
    }

    private fun drawCircles(canvas: Canvas, paint: Paint) {
        var centerRaw: Float
        var centerColumn: Float

        for (i in 0 until filas) {
            val pos = filas - i
            centerRaw = heightOfTile * (1 + 2 * i) / 2f
            for (j in 0 until columnas) {
                centerColumn = widthOfTile * (1 + 2 * j) / 2f
                paint.setColor(board!!, i, j)
                canvas.drawCircle(centerColumn, centerRaw, radio, paint)
                canvas.drawCircle(centerColumn, centerRaw, radio, linePaint2)
            }

        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (onPlayListener == null) {
            return super.onTouchEvent(event)
        }
        if (board!!.estado != Tablero.EN_CURSO) {
            Toast.makeText(this.context, R.string.round_already_finished, Toast.LENGTH_SHORT).show()
            return super.onTouchEvent(event)
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            try {
                onPlayListener?.onPlay(fromEventToI(event), fromEventToJ(event))
            } catch (e: Exception) {
                Toast.makeText(this.context, R.string.invalid_movement, Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private fun fromEventToI(event: MotionEvent): Int {
        val pos = (event.y / heightOfTile).toInt()
        return filas - pos - 1
    }

    private fun fromEventToJ(event: MotionEvent): Int {
        return (event.x / widthOfTile).toInt()
    }

    fun setOnPlayListener(listener: OnPlayListener) {
        this.onPlayListener = listener
    }

    fun setBoard(filas: Int, columnas: Int, board: TableroConecta4) {
        this.filas = filas
        this.columnas = columnas
        this.board = board
    }

}
