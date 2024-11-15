package com.example.reto1

import kotlin.random.Random

class TicTacToeGame {

    // Tablero de juego representado como un arreglo de caracteres
    private val mBoard = CharArray(9) { OPEN_SPOT }

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '  // Representa una casilla libre
    }

    private val mRand = Random(System.currentTimeMillis())

    /** Limpia el tablero de todos los X y O, configurando todas las casillas a OPEN_SPOT */
    fun clearBoard() {
        for (i in mBoard.indices) {
            mBoard[i] = OPEN_SPOT
        }
    }

    /** Configura el movimiento de un jugador en una ubicación específica del tablero. */
    fun setMove(player: Char, location: Int) {
        if (location in 0..8 && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player
        }
    }

    /** Devuelve la mejor jugada para la computadora. */
    fun getComputerMove(): Int {
        // Lógica para buscar una jugada ganadora o de bloqueo
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

        // Evita que el jugador humano gane en el siguiente turno
        for (i in mBoard.indices) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }

        // Genera un movimiento aleatorio si no hay jugada preferida
        var move: Int
        do {
            move = mRand.nextInt(9)
        } while (mBoard[move] != OPEN_SPOT)

        mBoard[move] = COMPUTER_PLAYER
        return move
    }

    /** Verifica si hay un ganador y devuelve el estado actual */
    fun checkForWinner(): Int {
        // Verifica victorias en filas
        for (i in 0..6 step 3) {
            if (mBoard[i] == mBoard[i + 1] && mBoard[i + 1] == mBoard[i + 2] && mBoard[i] != OPEN_SPOT) {
                return if (mBoard[i] == HUMAN_PLAYER) 2 else 3
            }
        }

        // Verifica victorias en columnas
        for (i in 0..2) {
            if (mBoard[i] == mBoard[i + 3] && mBoard[i + 3] == mBoard[i + 6] && mBoard[i] != OPEN_SPOT) {
                return if (mBoard[i] == HUMAN_PLAYER) 2 else 3
            }
        }

        // Verifica victorias en diagonales
        if ((mBoard[0] == mBoard[4] && mBoard[4] == mBoard[8] || mBoard[2] == mBoard[4] && mBoard[4] == mBoard[6]) && mBoard[4] != OPEN_SPOT) {
            return if (mBoard[4] == HUMAN_PLAYER) 2 else 3
        }

        // Verifica si es empate
        if (mBoard.all { it != OPEN_SPOT }) return 1

        return 0  // No hay ganador ni empate aún
    }
}