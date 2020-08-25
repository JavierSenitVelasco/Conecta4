package com.example.e339744.conecta4.model

import es.uam.eps.multij.ExcepcionJuego
import es.uam.eps.multij.Movimiento
import es.uam.eps.multij.Tablero
import java.util.ArrayList

/**
 * @author Javier Senit y Álvaro Ulloa
 *
 * Clase tablero, la cual creara el tablero con sus respectivas filas y columnas
 *
 * @param filas: numero de filas del tablero
 * @param columnas: numero de columnas del tablero
 */
class TableroConecta4(var filas: Int = 5, var columnas: Int = 7) : Tablero() {

    var tablero = arrayListOf<ArrayList<Int>>()
    var uMovimiento = arrayListOf<Int>()
    init {

        for (i in 0 until filas) {
            val auxArray = arrayListOf<Int>()
            for (j in 0 until columnas) {
                auxArray.add(0)
            }
            tablero.add(auxArray)
        }
        uMovimiento.add(-1)
        uMovimiento.add(-1)
        estado = EN_CURSO
    }


    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Funcion que imprime el tablero en cada instante del juego que sea necesario
     * @return cadena: tablero de juego
     */
    override fun toString(): String {
        var cadena: String = ""


        for (i in 0 until filas) {
            for (j in 0 until columnas) {
                cadena += "${tablero[i][j]} "
            }
            cadena += "\n"
        }
        return cadena
    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que lee el fichero que se le pasa para cargar el juego guardado anteriormente
     *
     * @param cadena: fichero de donde leer el tablero
     */
    override fun stringToTablero(cadena: String?) {
        val lines = cadena!!.split("\n")
        var aux = lines[0]

        turno = aux.toInt()

        aux = lines[1]

        estado = aux.toInt()


        var aux2 = lines[2].split(" ")

        if (aux2.size != 2){
            throw ExcepcionJuego("Formato de cadena invalida")
        }

        filas = aux2[0].toInt()
        columnas = aux2[1].toInt()

        var aux3 = lines[3].split(" ")

        if (aux3.size != 2){
            throw ExcepcionJuego("Formato de cadena invalida")
        }

        uMovimiento[0] = aux3[0].toInt()
        uMovimiento[1] = aux3[1].toInt()

        if (lines.size != filas + 5){
            throw ExcepcionJuego("Formato de cadena invalida")
        }

        for (x in 4 until lines.size - 1) {
            aux2 = lines[x].split(" ")
            if (aux2.size != columnas+1){
                throw ExcepcionJuego("Formato de cadena invalida")
            }
            for (j in 0 until columnas) {
                tablero[x - 4][j] = aux2[j].toInt()
            }


        }

    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que pasa el tablero a string para poder ser guardado
     * @return cadena: cadena que hara referencia al tablero que se tenga que cargar posteriormente
     */
    override fun tableroToString(): String {
        var tab = "$turno\n"
        tab += "$estado\n"
        tab += "$filas $columnas\n"
        tab += "${uMovimiento[0]} ${uMovimiento[1]}\n"
        for (i in 0 until filas) {
            for (j in 0 until columnas) {
                tab += "${tablero[i][j]} "
            }
            tab += "\n"
        }

        return tab
    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que devuelve cuales son todos los movimientos válidos en un determinado momento
     * @return movimientos: array con todos los movimientos válidos
     */
    override fun movimientosValidos(): ArrayList<Movimiento> {
        val movimientos = arrayListOf<Movimiento>()

        for (j in 0 until columnas) {
            var flag = 0
            for (i in 0 until filas) {
                if (tablero[i][j] == 0) {
                    flag = 1
                }
            }

            if (flag == 1) {
                movimientos.add(MovimientoConecta4(j))
            }

        }

        return movimientos
    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que realiza el movimiento por parte de un jugador
     * @param m: movimiento deseado que se comprueba si puede ser válido o no
     */
    override fun mueve(m: Movimiento?) {
        if (esValido(m)) {
            for (i in (0 until filas).reversed()) {
                if (tablero[i][m?.toString()?.toInt()!!] == 0) {
                    tablero[i][m.toString().toInt()] = turno + 1
                    uMovimiento[0] = i
                    uMovimiento[1] = m.toString().toInt()
                    break
                }
            }
            val ret = comprobarFinal()
            if (ret == 1) {
                estado = FINALIZADA
            }else if(ret == 2){
                estado = TABLAS
            } else{
                cambiaTurno()
            }
        } else {
            throw ExcepcionJuego("Movimiento inválido")
        }

    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que comprueba si un movimiento puede ser válido o no, teniendo en cuenta las dimensiones del tablero y si la casilla donde se dea mover esta ya ocupada
     *
     * @param filas: número de filas del tablero
     * @param columnas: número de columnas del tablero
     */
    override fun esValido(m: Movimiento?): Boolean {
        val posicion = m.toString().toInt()

        for (i in (0 until filas).reversed()) {
            if (tablero[i][posicion] == 0)
                return true
        }
        return false

    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que comprueba si existe ganador después de realizar un movimiento
     * @return 1 si hay ganador, 0 si no
     */
    fun comprobarFinal(): Int {

        if (comprobarHorizontal() == 1) {
            return 1
        }
        if (comprobarVertical() == 1) {
            return 1
        }
        if (comprobarDiagonal() == 1) {
            return 1
        }
        if (comprobarTablas() == 1){
            return 2
        }
        return 0
    }

    private fun comprobarTablas(): Int {
        var flag = 0

        for (i in 0 until filas) {
            for (j in 1 until columnas) {
                if (tablero[i][j] == 0) {
                    flag = 1
                }
            }
        }

        if (flag == 0){
            return 1
        }

        return 0
    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que comrpueba la jugada ganadora cuando se hace 4 en raya horizontal
     * @return 1 si hay 4 en raya horizontal, 0 si no
     */
    fun comprobarHorizontal(): Int {
        var contador: Int

        for (i in 0 until filas) {
            contador = 1
            var valor = tablero[i][0]
            for (j in 1 until columnas) {
                if (tablero[i][j] != 0) {
                    if (tablero[i][j] == valor) {
                        contador += 1
                    } else {
                        contador = 1
                        valor = tablero[i][j]
                    }
                    if (contador == 4) {
                        return 1
                    }
                } else {
                    contador = 0
                    valor = 0
                }
            }
        }

        return 0
    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que comrpueba la jugada ganadora cuando se hace 4 en raya vertical
     * @return 1 si hay 4 en raya vertical, 0 si no
     */
    fun comprobarVertical(): Int {
        var contador: Int

        for (i in 0 until columnas) {
            contador = 1
            var valor = tablero[0][i]
            for (j in 1 until filas) {
                if (tablero[j][i] != 0) {
                    if (tablero[j][i] == valor) {
                        contador += 1
                    } else {
                        contador = 1
                        valor = tablero[j][i]
                    }
                    if (contador == 4) {
                        return 1
                    }
                } else {
                    contador = 0
                    valor = 0
                }
            }
        }

        return 0
    }

    /**
     * @author Javier Senit y Álvaro Ulloa
     *
     * Función que comrpueba la jugada ganadora cuando se hace 4 en raya diagonal
     * @return 1 si hay 4 en raya diagonal, 0 si no
     */
    fun comprobarDiagonal(): Int {

        for (i in 0 until filas) {

            for (j in 0 until columnas) {
                if (tablero[i][j] != 0) {
                    if ((j <= columnas - 4) and (i <= filas - 4)) {
                        val d1 = tablero[i + 1][j + 1]
                        val d2 = tablero[i + 2][j + 2]
                        val d3 = tablero[i + 3][j + 3]

                        if ((tablero[i][j] == d1) and (d1 == d2) and (d2 == d3)) {
                            return 1
                        }
                    }

                    if ((j >= 3) and (i <= filas - 4)) {
                        val d1 = tablero[i + 1][j - 1]
                        val d2 = tablero[i + 2][j - 2]
                        val d3 = tablero[i + 3][j - 3]

                        if ((tablero[i][j] == d1) and (d1 == d2) and (d2 == d3)) {
                            return 1
                        }
                    }
                }
            }

        }

        return 0

    }


    fun getTablero(i: Int, j: Int): Int {
        return tablero[i][j]
    }

    override fun reset(): Boolean {
        super.reset()
        for (i in 0 until filas) {
            for (j in 0 until columnas) {
                tablero[i][j] = 0
            }
        }
        return true
    }


}