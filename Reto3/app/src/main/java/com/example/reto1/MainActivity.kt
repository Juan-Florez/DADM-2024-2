package com.example.reto1

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private lateinit var boardButtons: Array<Button>
    private lateinit var statusText: TextView
    private lateinit var newGameButton: Button
    private lateinit var scoreText: TextView

    private var humanWins = 0
    private var computerWins = 0
    private var ties = 0
    private var humanTurn = true // Indica si es el turno del jugador humano

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa el objeto TicTacToeGame
        mGame = TicTacToeGame()

        // Enlaza los elementos de la interfaz
        statusText = findViewById(R.id.statusText)
        scoreText = findViewById(R.id.scoreText)
        newGameButton = findViewById(R.id.newGameButton)

        // Configura los botones del tablero
        boardButtons = Array(9) { i ->
            findViewById<Button>(resources.getIdentifier("button$i", "id", packageName))
        }

        // Configura el botón de "New Game"
        newGameButton.setOnClickListener { startNewGame() }

        // Cargar marcador persistido
        loadScores()

        // Inicia un nuevo juego
        startNewGame()

        // Configura los listeners de cada botón
        for (i in boardButtons.indices) {
            boardButtons[i].setOnClickListener { onBoardClick(i) }
        }
    }

    private fun startNewGame() {
        mGame.clearBoard()
        for (button in boardButtons) {
            button.text = ""
            button.isEnabled = true
        }
        humanTurn = true
        statusText.text = getString(R.string.first_human)
    }

    private fun onBoardClick(position: Int) {
        if (humanTurn) {
            makeMove(TicTacToeGame.HUMAN_PLAYER, position)
            val winner = mGame.checkForWinner()
            if (winner == 0) {
                humanTurn = false
                statusText.text = getString(R.string.turn_computer)
                makeComputerMove()
            } else {
                endGame(winner)
            }
        }
    }

    private fun makeMove(player: Char, position: Int) {
        mGame.setMove(player, position)
        boardButtons[position].apply {
            text = player.toString()
            setTextColor(
                if (player == TicTacToeGame.HUMAN_PLAYER)
                    getColor(R.color.green) // Color verde para humano
                else
                    getColor(R.color.red) // Color rojo para computadora
            )
            isEnabled = false
        }
    }

    private fun makeComputerMove() {
        val computerMove = mGame.getComputerMove()
        makeMove(TicTacToeGame.COMPUTER_PLAYER, computerMove)
        val winner = mGame.checkForWinner()
        if (winner != 0) {
            endGame(winner)
        } else {
            humanTurn = true
            statusText.text = getString(R.string.turn_human)
        }
    }

    private fun endGame(winner: Int) {
        when (winner) {
            1 -> {
                statusText.text = getString(R.string.result_tie)
                ties++
            }
            2 -> {
                statusText.text = getString(R.string.result_human_wins)
                humanWins++
            }
            3 -> {
                statusText.text = getString(R.string.result_computer_wins)
                computerWins++
            }
        }

        // Actualiza el marcador
        updateScore()

        // Guarda los puntajes
        saveScores()

        // Desactiva todos los botones después de que termine el juego
        for (button in boardButtons) {
            button.isEnabled = false
        }
    }

    private fun updateScore() {
        scoreText.text = getString(
            R.string.score_format,
            humanWins,
            ties,
            computerWins
        )
    }

    private fun saveScores() {
        val sharedPrefs = getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putInt("humanWins", humanWins)
            putInt("computerWins", computerWins)
            putInt("ties", ties)
            apply()
        }
    }

    private fun loadScores() {
        val sharedPrefs = getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE)
        humanWins = sharedPrefs.getInt("humanWins", 0)
        computerWins = sharedPrefs.getInt("computerWins", 0)
        ties = sharedPrefs.getInt("ties", 0)
        updateScore()
    }
}




