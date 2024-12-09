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
import android.content.res.Configuration

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

    private var isMuted = false
    private lateinit var muteMenuItem: MenuItem

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

        //Actualizar estado del Mute
        isMuted = sharedPref.getBoolean("isMuted", false)

        // Actualizar la vista con los puntajes actuales
        updateScore()

        // Iniciar un nuevo juego
        startNewGame()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Cambia el layout a uno horizontal cuando está en orientación horizontal
            setContentView(R.layout.activity_main) // Asegúrate de tener un layout adecuado para landscape
            Toast.makeText(this, "Cambiando a orientación horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Cambia el layout a uno vertical cuando está en orientación vertical
            setContentView(R.layout.activity_main) // Asegúrate de tener un layout adecuado para portrait
            Toast.makeText(this, "Cambiando a orientación vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer)

        // Cargar estado del mute
        val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        isMuted = sharedPref.getBoolean("isMuted", false)  // Obtener estado guardado

        // Actualizar el volumen basado en el estado de mute
        if (isMuted) {
            mHumanMediaPlayer.setVolume(0f, 0f)  // Desactivar volumen
            mComputerMediaPlayer.setVolume(0f, 0f)
        } else {
            mHumanMediaPlayer.setVolume(1f, 1f)  // Restaurar volumen
            mComputerMediaPlayer.setVolume(1f, 1f)
        }
        // Actualizar el ícono del menú en función del estado de mute
        if (this::muteMenuItem.isInitialized) {
            if (isMuted) {
                muteMenuItem.setIcon(R.drawable.ic_mute)  // Ícono de mute
            } else {
                muteMenuItem.setIcon(R.drawable.ic_unmute)  // Ícono de sonido
            }
        }

        // Cargar el nivel de dificultad
        val difficultyOrdinal =
            sharedPref.getInt("difficultyLevel", TicTacToeGame.DifficultyLevel.Easy.ordinal)
        val difficultyLevel = TicTacToeGame.DifficultyLevel.values()[difficultyOrdinal]
        mGame.setDifficultyLevel(difficultyLevel)  // Establecer el nivel de dificultad
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

        // Obtener la referencia al ítem de mute
        muteMenuItem = menu?.findItem(R.id.mute_sound) ?: return super.onCreateOptionsMenu(menu)

        // Actualizar el ícono según el estado del mute
        if (isMuted) {
            muteMenuItem.setIcon(R.drawable.ic_mute)  // Ícono de mute
        } else {
            muteMenuItem.setIcon(R.drawable.ic_unmute)  // Ícono de sonido
        }
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

            R.id.reset_scores -> {
                resetScores()
                return true
            }

            R.id.mute_sound -> {
                toggleMute(item) // Llamamos a la función para alternar el mute
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun resetScores() {
        humanWins = 0
        androidWins = 0
        ties = 0
        updateScore() // Actualiza la interfaz con los nuevos puntajes
        Toast.makeText(this, "Scores reset!", Toast.LENGTH_SHORT).show()
    }

    private fun toggleMute(item: MenuItem) {
        isMuted = !isMuted // Cambiar el estado del mute

        // Guardar el estado de mute en SharedPreferences
        val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isMuted", isMuted)
            apply()  // Guardar de forma asíncrona
        }

        // Actualizar el ícono del menú
        if (isMuted) {
            item.setIcon(R.drawable.ic_mute) // Cambiar al ícono de mute
            // Desactivar el sonido
            mHumanMediaPlayer.setVolume(0f, 0f)  // Desactivar volumen
            mComputerMediaPlayer.setVolume(0f, 0f)
        } else {
            item.setIcon(R.drawable.ic_unmute) // Cambiar al ícono de sonido
            // Activar el sonido
            mHumanMediaPlayer.setVolume(1f, 1f)  // Restaurar volumen
            mComputerMediaPlayer.setVolume(1f, 1f)
        }
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

                // Guardar el nivel de dificultad en SharedPreferences
                val sharedPref = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("difficultyLevel", selectedDifficulty.ordinal) // Guardamos el ordinal del nivel de dificultad
                    apply()
                }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("gameBoard", mGame.getBoard()) // Guardar el tablero
        outState.putBoolean("humanTurn", humanTurn)          // Guardar turno
        outState.putInt("humanWins", humanWins)              // Guardar puntajes
        outState.putInt("androidWins", androidWins)
        outState.putInt("ties", ties)
        outState.putBoolean("gameOver", mGameOver)           // Guardar estado del juego
        outState.putBoolean("isMuted", isMuted)              // Guardar el estado del mute
        outState.putInt("difficultyLevel", mGame.getDifficultyLevel().ordinal) // Guardar nivel de dificultad
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mGame.setBoard(savedInstanceState.getCharArray("gameBoard")!!)
        humanTurn = savedInstanceState.getBoolean("humanTurn")
        humanWins = savedInstanceState.getInt("humanWins")
        androidWins = savedInstanceState.getInt("androidWins")
        ties = savedInstanceState.getInt("ties")
        mGameOver = savedInstanceState.getBoolean("gameOver")

        isMuted = savedInstanceState.getBoolean("isMuted")  // Restaurar el estado del mute
        val difficultyOrdinal = savedInstanceState.getInt("difficultyLevel", TicTacToeGame.DifficultyLevel.Easy.ordinal)
        val difficultyLevel = TicTacToeGame.DifficultyLevel.values()[difficultyOrdinal]
        mGame.setDifficultyLevel(difficultyLevel)  // Restaurar el nivel de dificultad

        mBoardView.invalidate() // Redibuja el tablero con el estado restaurado
        updateScore()           // Actualiza los puntajes
        statusTextView.text = if (humanTurn) {
            getString(R.string.turn_human)
        } else {
            getString(R.string.turn_computer)
        }
    }

}





