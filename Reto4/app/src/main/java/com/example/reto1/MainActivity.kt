package com.example.reto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private lateinit var boardButtons: Array<Button>
    private lateinit var statusTextView: TextView
    private lateinit var scoreTextView: TextView

    private var humanTurn = true  // Indica si es el turno del jugador humano
    private var humanWins = 0
    private var androidWins = 0
    private var ties = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Carga los puntajes guardados desde SharedPreferences
        val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        humanWins = sharedPref.getInt("humanWins", 0)  // Valor predeterminado 0 si no hay datos
        androidWins = sharedPref.getInt("androidWins", 0)
        ties = sharedPref.getInt("ties", 0)

        // Inicializa el objeto TicTacToeGame
        mGame = TicTacToeGame()

        // Configura los botones del tablero
        boardButtons = Array(9) { i ->
            findViewById(resources.getIdentifier("button$i", "id", packageName))
        }

        statusTextView = findViewById(R.id.statusText)
        scoreTextView = findViewById(R.id.scoreText)

        // Inicializa los valores de puntuación
        updateScore()

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
        statusTextView.text = getString(R.string.first_human)
    }

    private fun onBoardClick(position: Int) {
        if (humanTurn) {
            makeMove(TicTacToeGame.HUMAN_PLAYER, position)
            val winner = mGame.checkForWinner()
            if (winner == 0) {
                humanTurn = false
                statusTextView.text = getString(R.string.turn_computer)
                makeComputerMove()
            } else {
                endGame(winner)
            }
        }
    }

    private fun makeMove(player: Char, position: Int) {
        mGame.setMove(player, position)
        boardButtons[position].text = player.toString()
        boardButtons[position].isEnabled = false
        boardButtons[position].setTextColor(
            if (player == TicTacToeGame.HUMAN_PLAYER) getColor(R.color.green)
            else getColor(R.color.red)
        )
    }

    private fun makeComputerMove() {
        val computerMove = mGame.getComputerMove()
        makeMove(TicTacToeGame.COMPUTER_PLAYER, computerMove)
        val winner = mGame.checkForWinner()
        if (winner != 0) {
            endGame(winner)
        } else {
            humanTurn = true
            statusTextView.text = getString(R.string.turn_human)
        }
    }

    private fun endGame(winner: Int) {
        when (winner) {
            1 -> {
                ties++
                statusTextView.text = getString(R.string.result_tie)
            }
            2 -> {
                humanWins++
                statusTextView.text = getString(R.string.result_human_wins)
            }
            3 -> {
                androidWins++
                statusTextView.text = getString(R.string.result_computer_wins)
            }
        }
        updateScore()

        // Desactiva todos los botones después de que termine el juego
        for (button in boardButtons) {
            button.isEnabled = false
        }
    }

    private fun updateScore() {
        // Guarda los puntajes en SharedPreferences
        val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("humanWins", humanWins)
            putInt("androidWins", androidWins)
            putInt("ties", ties)
            apply() // Guarda los datos de forma asíncrona
        }

        // Actualiza la vista con los puntajes actuales
        scoreTextView.text = getString(R.string.score_format, humanWins, ties, androidWins)
    }

    // Opciones de menú
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                showAboutDialog()
                true
            }
            R.id.new_game -> {
                startNewGame()
                return true
            }
            R.id.ai_difficulty -> {
                showDifficultyDialog()
                return true
            }
            R.id.quit -> {
                // Muestra un cuadro de diálogo de confirmación
                AlertDialog.Builder(this)
                    .setTitle(R.string.quit_title) // Título del diálogo
                    .setMessage(R.string.quit_message) // Mensaje del diálogo
                    .setPositiveButton(R.string.yes) { _, _ ->
                        // Finaliza la actividad
                        finish()
                    }
                    .setNegativeButton(R.string.no, null) // Cierra el cuadro de diálogo
                    .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.about_dialog, null)

        builder.setView(view)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showDifficultyDialog() {
        val difficultyLevels = arrayOf(
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_harder),
            getString(R.string.difficulty_expert)
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.difficulty_title) // Usamos el texto de strings.xml
            .setSingleChoiceItems(difficultyLevels, mGame.getDifficultyLevel().ordinal) { dialog, which ->
                val selectedDifficulty = TicTacToeGame.DifficultyLevel.values()[which]
                mGame.setDifficultyLevel(selectedDifficulty)

                // Mostrar un mensaje de confirmación usando los textos desde strings.xml
                val message = when (selectedDifficulty) {
                    TicTacToeGame.DifficultyLevel.Easy -> getString(R.string.difficulty_easy)
                    TicTacToeGame.DifficultyLevel.Harder -> getString(R.string.difficulty_harder)
                    TicTacToeGame.DifficultyLevel.Expert -> getString(R.string.difficulty_expert)
                }

                Toast.makeText(this, "Difficulty set to $message", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create()
            .show()
    }

}





