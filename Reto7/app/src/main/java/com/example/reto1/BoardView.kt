package com.example.reto1

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Constante para el grosor de las líneas del tablero
    companion object {
        const val GRID_WIDTH = 6
    }

    private lateinit var mHumanBitmap: Bitmap
    private lateinit var mComputerBitmap: Bitmap
    private lateinit var mPaint: Paint

    init {
        initialize()
    }

    private fun initialize() {
        // Carga las imágenes X y O desde los recursos
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.x_img)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.o_img)

        // Inicializa el objeto Paint
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            strokeWidth = GRID_WIDTH.toFloat()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val boardWidth = width
        val boardHeight = height
        val cellWidth = boardWidth / 3

        // Dibujar las líneas verticales
        canvas.drawLine(cellWidth.toFloat(), 0f, cellWidth.toFloat(), boardHeight.toFloat(), mPaint)
        canvas.drawLine((cellWidth * 2).toFloat(), 0f, (cellWidth * 2).toFloat(), boardHeight.toFloat(), mPaint)

        // Dibujar las líneas horizontales
        canvas.drawLine(0f, cellWidth.toFloat(), boardWidth.toFloat(), cellWidth.toFloat(), mPaint)
        canvas.drawLine(0f, (cellWidth * 2).toFloat(), boardWidth.toFloat(), (cellWidth * 2).toFloat(), mPaint)

        // Dibujar X y O en el tablero
        for (i in 0 until 9) {
            val col = i % 3
            val row = i / 3
            val left = col * cellWidth
            val top = row * cellWidth
            val right = left + cellWidth
            val bottom = top + cellWidth

            if (::mGame.isInitialized && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap, null, Rect(left, top, right, bottom), null)
            } else if (::mGame.isInitialized && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap, null, Rect(left, top, right, bottom), null)
            }
        }
    }

    // Declarar mGame y su setter
    private lateinit var mGame: TicTacToeGame

    fun setGame(game: TicTacToeGame) {
        mGame = game
    }

    fun getBoardCellWidth(): Int {
        return width / 3
    }

    fun getBoardCellHeight(): Int {
        return height / 3
    }
}
