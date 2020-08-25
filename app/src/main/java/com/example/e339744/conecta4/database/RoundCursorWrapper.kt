package com.example.e339744.conecta4.database

import android.database.Cursor
import android.database.CursorWrapper
import android.util.Log
import com.example.e339744.conecta4.model.Round
import es.uam.eps.multij.ExcepcionJuego

class RoundCursorWrapper(cursor: Cursor): CursorWrapper(cursor){
    private val DEBUG = "DEBUG"
    val round: Round

    get(){
        val playername = getString(getColumnIndex(RoundDatabaseSchema.UserTable.Cols.PLAYERNAME))
        val playeruuid = getString(getColumnIndex(RoundDatabaseSchema.UserTable.Cols.PLAYERUUID))
        val rounduuid = getString(getColumnIndex(RoundDatabaseSchema.RoundTable.Cols.ROUNDUUID))
        val date = getString(getColumnIndex(RoundDatabaseSchema.RoundTable.Cols.DATE))
        val title = getString(getColumnIndex(RoundDatabaseSchema.RoundTable.Cols.TITLE))
        val filas = getString(getColumnIndex(RoundDatabaseSchema.RoundTable.Cols.FILAS))
        val columnas = getString(getColumnIndex(RoundDatabaseSchema.RoundTable.Cols.COLUMNAS))
        val board = getString(getColumnIndex(RoundDatabaseSchema.RoundTable.Cols.BOARD))
        val round = Round(filas.toInt(), columnas.toInt())
        round.firstPlayerName= "CPU"
        round.firstPlayerUUID= "CPU"
        round.secondPlayerName= playername
        round.secondPlayerUUID= playeruuid
        round.id= rounduuid
        round.date= date
        round.title= title
        try{
            round.board.stringToTablero(board)
        }catch(e: ExcepcionJuego){
            Log.d(DEBUG, "Error al pasar la cadena al tablero")
        }
        return round
    }


}