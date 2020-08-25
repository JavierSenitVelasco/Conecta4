package com.example.e339744.conecta4.database

object RoundDatabaseSchema{
    object UserTable{
        val NAME = "users"
        object Cols{
            val PLAYERUUID = "playeruuid1"
            val PLAYERNAME = "playername"
            val PLAYERPASSWORD = "playerpassword"
        }
    }

    object RoundTable{
        val NAME = "rounds"
        object Cols{
            val PLAYERUUID = "playeruuid2"
            val ROUNDUUID = "rounduuid"
            val DATE = "date"
            val TITLE = "title"
            val FILAS = "filas"
            val COLUMNAS = "columnas"
            val BOARD = "board"
        }
    }
}