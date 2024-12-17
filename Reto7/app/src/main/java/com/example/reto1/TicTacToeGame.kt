package com.example.reto1

import com.google.firebase.database.DatabaseReference
import kotlin.random.Random

class TicTacToeGame {

    // Enumeration for difficulty levels
    enum class DifficultyLevel { Easy, Harder, Expert }

    // Current difficulty level
    private var mDifficultyLevel = DifficultyLevel.Expert

    // Board representation
    private val mBoard = CharArray(BOARD_SIZE) { ' ' }

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val EMPTY_CELL = ' '  // Representación de un espacio vacío
        const val BOARD_SIZE = 9
    }

    private val mRand = Random(System.currentTimeMillis())

    // Getter and Setter for difficulty level
    fun getDifficultyLevel(): DifficultyLevel {
        return mDifficultyLevel
    }

    fun setDifficultyLevel(difficultyLevel: DifficultyLevel) {
        mDifficultyLevel = difficultyLevel
    }

    // Clears the board
    fun clearBoard() {
        for (i in mBoard.indices) {
            mBoard[i] = EMPTY_CELL
        }
    }

    // Sets a move on the board
    fun setMove(player: Char, location: Int): Boolean {
        if (location in 0 until BOARD_SIZE && mBoard[location] == EMPTY_CELL) {
            mBoard[location] = player
            return true
        }
        return false
    }

    // Checks if the game is over
    fun isGameOver(): Boolean {
        return checkForWinner() != -1 || mBoard.all { it != EMPTY_CELL }
    }

    // Returns the board state
    fun getBoard(): CharArray {
        return mBoard.copyOf() // Devuelve una copia del tablero
    }

    // Restores the board state
    fun setBoard(newBoard: CharArray) {
        if (newBoard.size == BOARD_SIZE) {
            for (i in mBoard.indices) {
                mBoard[i] = newBoard[i]
            }
        } else {
            throw IllegalArgumentException("El tamaño del tablero no es válido")
        }
    }

    fun getBoardOccupant(position: Int): Char {
        return mBoard[position]
    }

    // Returns the computer's move based on difficulty level
    fun getComputerMove(): Int {
        return when (mDifficultyLevel) {
            DifficultyLevel.Easy -> getRandomMove()
            DifficultyLevel.Harder -> getWinningMove() ?: getRandomMove()
            DifficultyLevel.Expert -> getWinningMove() ?: getBlockingMove() ?: getRandomMove()
        }
    }

    // Generates a random move
    private fun getRandomMove(): Int {
        val availableMoves = mBoard.indices.filter { mBoard[it] == EMPTY_CELL }
        return if (availableMoves.isNotEmpty()) availableMoves.random() else -1
    }

    // Finds a winning move for the computer
    private fun getWinningMove(): Int? {
        for (i in mBoard.indices) {
            if (mBoard[i] == EMPTY_CELL) {
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = EMPTY_CELL // Undo move
                    return i
                }
                mBoard[i] = EMPTY_CELL
            }
        }
        return null
    }

    // Finds a blocking move to prevent the human from winning
    private fun getBlockingMove(): Int? {
        for (i in mBoard.indices) {
            if (mBoard[i] == EMPTY_CELL) {
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 1) {
                    mBoard[i] = EMPTY_CELL // Undo move
                    return i
                }
                mBoard[i] = EMPTY_CELL
            }
        }
        return null
    }

    // Checks for the winner
    fun checkForWinner(): Int {
        val winningLines = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8), // Rows
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8), // Columns
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)                       // Diagonals
        )

        for (line in winningLines) {
            if (mBoard[line[0]] != EMPTY_CELL &&
                mBoard[line[0]] == mBoard[line[1]] &&
                mBoard[line[1]] == mBoard[line[2]]
            ) {
                return if (mBoard[line[0]] == HUMAN_PLAYER) 1 else 2
            }
        }

        return if (mBoard.all { it != EMPTY_CELL }) 0 else -1 // Return 0 if it's a tie, -1 if the game continues
    }

}
