package com.example.e339744.conecta4.model

import java.lang.Exception

/*object RoundRepository {
    val rounds = ArrayList<Round>()

    init {
    }

    fun getRound(id: String): Round {
        val round = rounds.find { it.id == id }
        return round ?: throw Exception("Round not found.")
    }

    fun addRound(filas:Int, columnas: Int, nombre: String) {
        rounds.add(Round(filas, columnas, nombre))
    }
}*/


interface RoundRepository {
    @Throws(Exception::class)
    fun open()
    fun close()
    interface LoginRegisterCallback{
        fun onLogin(playerUUID: String)
        fun onError(error: String)
    }
    fun login(playername: String, password: String, callback:LoginRegisterCallback)
    fun register(playername: String, password: String, callback:LoginRegisterCallback)

    interface BooleanCallback{
        fun onResponse(ok: Boolean)
    }

    fun getRounds(playerUUID: String, orderByField: String, group:String, callback:RoundsCallback)
    fun addRound(round: Round, callback: BooleanCallback)
    fun updateRound(round: Round, callback: BooleanCallback)
    interface RoundsCallback{
        fun onResponse(rounds: List<Round>)
        fun onError(error: String)
    }
}