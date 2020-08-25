package com.example.e339744.conecta4.model

import es.uam.eps.multij.Movimiento

/**
 * @author Javier Senit y Álvaro Ulloa
 * Clase que crea un movimiento para el juego
 *
 * @param columna: numero de columna en la que se ejecuta el movimiento
 */
class MovimientoConecta4(var columna: Int = -1) : Movimiento() {

    override fun equals(other: Any?): Boolean {
        return this.columna == (other as MovimientoConecta4).columna
    }

    override fun toString() = columna.toString()

}
