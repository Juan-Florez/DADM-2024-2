package com.example.reto1

import kotlin.random.Random

class TicTacToeGame {

    // Enumeration for difficulty levels
    enum class DifficultyLevel { Easy, Harder, Expert }

    // Current difficulty level
    private var mDifficultyLevel = DifficultyLevel.Expert

    // Board representation
    private val mBoard = CharArray(9) { OPEN_SPOT }

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' ' // Represents an empty cell
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
            mBoard[i] = OPEN_SPOT
        }
    }

    // Sets a move on the board
    fun setMove(player: Char, location: Int) {
        if (location in 0..8 && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player
        }
    }

    // Returns the computer's move based on difficulty level
    fun getComputerMove(): Int {
        return when (mDifficultyLevel) {
            DifficultyLevel.Easy -> getRandomMove()
            DifficultyLevel.Harder -> getWinningMove().takeIf { it != -1 } ?: getRandomMove()
            DifficultyLevel.Expert -> {
                getWinningMove().takeIf { it != -1 }
                    ?: getBlockingMove().takeIf { it != -1 }
                    ?: getRandomMove()
            }
        }
    }

    // Generates a random move
    private fun getRandomMove(): Int {
        var move: Int
        do {
            move = mRand.nextInt(9)
        } while (mBoard[move] != OPEN_SPOT)
        return move
    }

    // Finds a winning move for the computer
    private fun getWinningMove(): Int {
        for (i in mBoard.indices) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }
        return -1
    }

    // Finds a blocking move to prevent the human from winning
    private fun getBlockingMove(): Int {
        for (i in mBoard.indices) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }
        return -1
    }

    // Checks for the winner
    fun checkForWinner(): Int {
        // Check rows
        for (i in 0..6 step 3) {
            if (mBoard[i] == mBoard[i + 1] && mBoard[i + 1] == mBoard[i + 2] && mBoard[i] != OPEN_SPOT) {
                return if (mBoard[i] == HUMAN_PLAYER) 2 else 3
            }
        }

        // Check columns
        for (i in 0..2) {
            if (mBoard[i] == mBoard[i + 3] && mBoard[i + 3] == mBoard[i + 6] && mBoard[i] != OPEN_SPOT) {
                return if (mBoard[i] == HUMAN_PLAYER) 2 else 3
            }
        }

        // Check diagonals
        if ((mBoard[0] == mBoard[4] && mBoard[4] == mBoard[8] || mBoard[2] == mBoard[4] && mBoard[4] == mBoard[6]) && mBoard[4] != OPEN_SPOT) {
            return if (mBoard[4] == HUMAN_PLAYER) 2 else 3
        }

        // Check for tie
        if (mBoard.all { it != OPEN_SPOT }) return 1

        return 0 // No winner yet
    }
}
