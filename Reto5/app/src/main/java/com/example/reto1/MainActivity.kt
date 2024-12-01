package com.example.reto1

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private lateinit var mBoardView: BoardView
    private lateinit var statusTextView: TextView
    private lateinit var scoreTextView: TextView

    private lateinit var mHumanMediaPlayer: MediaPlayer
    private lateinit var mComputerMediaPlayer: MediaPlayer

    private var mGameOver = false
    private var humanTurn = true  // Indica si es el turno del jugador humano
    private var humanWins = 0
    private var androidWins = 0
    private var ties = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de objetos
        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.board)
        statusTextView = findViewById(R.id.statusText)
        scoreTextView = findViewById(R.id.scoreText)

        // Configuración de BoardView y su Listener
        mBoardView.setGame(mGame)
        mBoardView.setOnTouchListener(mTouchListener)

        // Cargar puntajes desde SharedPreferences
        val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        humanWins = sharedPref.getInt("humanWins", 0)
        androidWins = sharedPref.getInt("androidWins", 0)
        ties = sharedPref.getInt("ties", 0)

        // Actualizar la vista con los puntajes actuales
        updateScore()

        // Iniciar un nuevo juego
        startNewGame()
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }

    private fun playHumanSound() {
        if (mHumanMediaPlayer.isPlaying) {
            mHumanMediaPlayer.seekTo(0) // Reinicia el sonido si ya está sonando
        }
        mHumanMediaPlayer.start() // Reproduce el sonido
    }

    private fun playComputerSound() {
        if (mComputerMediaPlayer.isPlaying) {
            mComputerMediaPlayer.seekTo(0) // Reinicia el sonido si ya está sonando
        }
        mComputerMediaPlayer.start() // Reproduce el sonido
    }

    private fun startNewGame() {
        mGame.clearBoard()
        mBoardView.invalidate()  // Redibuja el tablero
        humanTurn = true
        statusTextView.text = getString(R.string.first_human)
    }

    private val handler = Handler(Looper.getMainLooper()) // Usamos el Looper del hilo principal


    private val mTouchListener = View.OnTouchListener { v, event ->
        // Verifica que el juego no haya terminado y que sea el turno del jugador humano
        if (humanTurn && !mGame.isGameOver()) {
            val col = (event.x / mBoardView.getBoardCellWidth()).toInt()
            val row = (event.y / mBoardView.getBoardCellHeight()).toInt()
            val position = row * 3 + col

            if (mGame.setMove(TicTacToeGame.HUMAN_PLAYER, position)) {
                mBoardView.invalidate() // Redibuja el tablero con el movimiento humano
                playHumanSound() // Sonido cuando juega el humano
                val winner = mGame.checkForWinner()
                when (winner) {
                    0 -> {  // Empate
                        endGame(winner)
                    }
                    -1 -> { // El juego sigue
                        humanTurn = false
                        statusTextView.text = getString(R.string.turn_computer)

                        // Retraso de 1 segundo antes de que la computadora haga su movimiento
                        handler.postDelayed({
                            makeComputerMove() // Llama a la función para que la computadora juegue después de un segundo
                        }, 1000) // 1000 ms = 1 segundo
                    }
                    else -> { // Hay un ganador
                        endGame(winner)
                    }
                }
            }
        }
        false
    }

    private fun makeComputerMove() {
        if (!mGame.isGameOver()) {
            val computerMove = mGame.getComputerMove() // Obtiene el movimiento de la computadora
            if (computerMove != -1) {
                mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, computerMove)
                mBoardView.invalidate() // Redibuja el tablero con el movimiento de la computadora
                playComputerSound()  // Play computer move sound

                val winner = mGame.checkForWinner()
                if (winner != -1) { // Si hay un ganador, termina el juego
                    endGame(winner)
                } else { // Si no hay ganador, el turno vuelve al jugador humano
                    humanTurn = true
                    statusTextView.text = getString(R.string.turn_human)
                }
            }
        }
    }



    private fun endGame(winner: Int) {
        when (winner) {
            0 -> {  // Empate
                ties++
                statusTextView.text = getString(R.string.result_tie)
            }
            1 -> { // El jugador humano gana
                humanWins++
                statusTextView.text = getString(R.string.result_human_wins)
            }
            2 -> { // La computadora gana
                androidWins++
                statusTextView.text = getString(R.string.result_computer_wins)
            }
        }
        updateScore() // Actualiza los puntajes
        mGameOver = true  // Desactivar el juego después de que termine
    }

    private fun updateScore() {
        // Guardar puntajes en SharedPreferences
        val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("humanWins", humanWins)
            putInt("androidWins", androidWins)
            putInt("ties", ties)
            apply() // Guardar de forma asíncrona
        }

        // Actualizar la vista con los puntajes actuales
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
                return true
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
                // Cuadro de diálogo de confirmación
                AlertDialog.Builder(this)
                    .setTitle(R.string.quit_title)
                    .setMessage(R.string.quit_message)
                    .setPositiveButton(R.string.yes) { _, _ -> finish() }
                    .setNegativeButton(R.string.no, null)
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
            .setTitle(R.string.difficulty_title)
            .setSingleChoiceItems(difficultyLevels, mGame.getDifficultyLevel().ordinal) { dialog, which ->
                val selectedDifficulty = TicTacToeGame.DifficultyLevel.values()[which]
                mGame.setDifficultyLevel(selectedDifficulty)
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





