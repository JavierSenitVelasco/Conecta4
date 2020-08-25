package com.example.e339744.conecta4.model

import org.json.JSONObject
import java.util.*


class Round( val filas: Int = 5,  val columnas: Int = 7) {
     var id: String = ""
     var title: String = ""
     var date: String = ""
     var board: TableroConecta4 = TableroConecta4(5, 7)
     lateinit var firstPlayerName: String
     lateinit var firstPlayerUUID: String
     lateinit var secondPlayerName: String
     lateinit var secondPlayerUUID: String


    init {
        id = UUID.randomUUID().toString()
        title = "ROUND ${id.toString().substring(19, 23).toUpperCase()}"
        date = Date().toString()
        board = TableroConecta4(filas, columnas)
    }

    fun toJSONString(): String {
        val json = JSONObject()
        json.put("id", id)
        json.put("title", title)
        json.put("date", date)
        json.put("filas", filas)
        json.put("columnas", columnas)
        json.put("boardString", board.tableroToString())
        json.put("firstPlayerName", firstPlayerName)
        json.put("firstPlayerUUID", firstPlayerUUID)
        json.put("secondPlayerName", secondPlayerName)
        json.put("secondPlayerUUID", secondPlayerUUID)
        return json.toString()
    }


    companion object {
        fun fromJSONString(string: String): Round {
            val jsonObject = JSONObject(string)
            val round = Round(jsonObject.get("filas") as Int, jsonObject.get("columnas") as Int)
            round.id = jsonObject.get("id") as String
            round.title = jsonObject.get("title") as String
            round.date = jsonObject.get("date") as String
            round.board.stringToTablero(jsonObject.get("boardString") as String)
            round.firstPlayerName = jsonObject.get("firstPlayerName") as String
            round.firstPlayerUUID = jsonObject.get("firstPlayerUUID") as String
            round.secondPlayerName = jsonObject.get("secondPlayerName") as String
            round.secondPlayerUUID = jsonObject.get("secondPlayerUUID") as String
            return round
        }
    }

}
