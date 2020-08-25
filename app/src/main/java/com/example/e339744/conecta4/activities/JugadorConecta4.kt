package com.example.e339744.conecta4.activities

import android.view.View
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.MovimientoConecta4
import com.example.e339744.conecta4.views.ViewConecta4
import es.uam.eps.multij.*


class JugadorConecta4(nombre: String) : ViewConecta4.OnPlayListener, Jugador {
    private val FILAS = 6
    private val COLUMNAS = 7
    private val ids = arrayOf(
        intArrayOf(R.id.f00, R.id.f01, R.id.f02, R.id.f03, R.id.f04, R.id.f05, R.id.f06),
        intArrayOf(R.id.f10, R.id.f11, R.id.f12, R.id.f13, R.id.f14, R.id.f15, R.id.f16),
        intArrayOf(R.id.f20, R.id.f21, R.id.f22, R.id.f23, R.id.f24, R.id.f25, R.id.f26),
        intArrayOf(R.id.f30, R.id.f31, R.id.f32, R.id.f33, R.id.f34, R.id.f35, R.id.f36),
        intArrayOf(R.id.f40, R.id.f41, R.id.f42, R.id.f43, R.id.f44, R.id.f45, R.id.f46),
        intArrayOf(R.id.f50, R.id.f51, R.id.f52, R.id.f53, R.id.f54, R.id.f55, R.id.f56)
    )

    private lateinit var game: Partida

    fun setPartida(game: Partida) {
        this.game = game
    }

    override fun onPlay(row: Int, column: Int) {
        if (game.tablero.estado != Tablero.EN_CURSO) {
            return
        }
        val m = MovimientoConecta4(column)
        game.realizaAccion(AccionMover(this, m))
    }


    override fun getNombre() = "4ER local player"

    override fun puedeJugar(p0: Tablero?) = true

    override fun onCambioEnPartida(p0: Evento) {}

    private fun fromViewToJ(view: View): Int {

        for (i in 0 until FILAS) {
            for (j in 0 until COLUMNAS) {
                if (view.id == ids[i][j]) {
                    return j
                }
            }
        }
        return -1
    }


}