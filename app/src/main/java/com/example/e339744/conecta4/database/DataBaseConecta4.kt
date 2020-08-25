package com.example.e339744.conecta4.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.RoundRepository
import java.util.*
import kotlin.collections.ArrayList


class DataBaseConecta4(context: Context) : RoundRepository {

    private val DEBUG_TAG = "DEBUG"
    private val helper: DatabaseHelper
    private var db: SQLiteDatabase? = null

    init {
        helper = DatabaseHelper(context)
    }

    private class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            createTable(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS" + RoundDatabaseSchema.UserTable.NAME)
            db.execSQL("DROP TABLE IF EXISTS" + RoundDatabaseSchema.RoundTable.NAME)
            createTable(db)
        }

        private fun createTable(db: SQLiteDatabase) {
            val str1 = ("CREATE TABLE " + RoundDatabaseSchema.UserTable.NAME +
                    " (" + "_id integer primary key autoincrement, " + RoundDatabaseSchema.UserTable.Cols.PLAYERUUID + " TEXT UNIQUE, "
                    + RoundDatabaseSchema.UserTable.Cols.PLAYERNAME + " TEXT UNIQUE, " + RoundDatabaseSchema.UserTable.Cols.PLAYERPASSWORD + " TEXT);")
            val str2 =
                ("CREATE TABLE " + RoundDatabaseSchema.RoundTable.NAME + " (" + "_id integer primary key autoincrement, "
                        + RoundDatabaseSchema.RoundTable.Cols.ROUNDUUID + " TEXT UNIQUE, " + RoundDatabaseSchema.RoundTable.Cols.PLAYERUUID
                        + " TEXT REFERENCES " + RoundDatabaseSchema.UserTable.Cols.PLAYERUUID + ", " + RoundDatabaseSchema.RoundTable.Cols.DATE
                        + " TEXT, " + RoundDatabaseSchema.RoundTable.Cols.TITLE + " TEXT, " + RoundDatabaseSchema.RoundTable.Cols.FILAS
                        + " TEXT, " + RoundDatabaseSchema.RoundTable.Cols.COLUMNAS + " TEXT, " + RoundDatabaseSchema.RoundTable.Cols.BOARD + " TEXT);")
            try {
                db.execSQL(str1)
                db.execSQL(str2)
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

    }

    @Throws(SQLException::class)
    override fun open() {
        db = helper.writableDatabase
    }

    override fun close() {
        db?.close()
    }

    override fun login(playername: String, playerpassword: String, callback: RoundRepository.LoginRegisterCallback) {
        Log.d(DEBUG_TAG, "Login $playername")
        val cursor = db!!.query(
            RoundDatabaseSchema.UserTable.NAME,
            arrayOf(RoundDatabaseSchema.UserTable.Cols.PLAYERUUID),
            RoundDatabaseSchema.UserTable.Cols.PLAYERNAME + " = ? AND "
                    + RoundDatabaseSchema.UserTable.Cols.PLAYERPASSWORD + " = ?",
            arrayOf(playername, playerpassword),
            null, null, null
        )

        val count = cursor.count
        val uuid =
            if (count == 1 && cursor.moveToFirst()) cursor.getString(0) else ""
        cursor.close()
        if (count == 1)
            callback.onLogin(uuid)
        else
            callback.onError("Usuario o contraseña incorrectos.")
    }

    override fun register(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {

        val values = ContentValues()
        val uuid = UUID.randomUUID().toString()
        values.put(RoundDatabaseSchema.UserTable.Cols.PLAYERUUID, uuid)
        values.put(RoundDatabaseSchema.UserTable.Cols.PLAYERNAME, playername)
        values.put(RoundDatabaseSchema.UserTable.Cols.PLAYERPASSWORD, password)
        val id = db!!.insert(RoundDatabaseSchema.UserTable.NAME, null, values)
        if (id < 0)
            callback.onError("Error al insertar el nuevo jugador llamado $playername")
        else
            callback.onLogin(uuid)


    }

    override fun getRounds(
        playerUUID: String,
        orderByField: String,
        group: String,
        callback: RoundRepository.RoundsCallback
    ) {
        val rounds = ArrayList<Round>()
        val cursor = queryRounds()

        cursor.moveToFirst()
        while(!cursor.isAfterLast()){
            val round = cursor.round
            if(round.secondPlayerUUID.equals(playerUUID))
                rounds.add(round)
            cursor.moveToNext()
        }
        cursor.close()
        if (cursor.count > 0)
            callback.onResponse(rounds)
        else
            callback.onError("No se han encontrado partdas en la base de datos")

    }


    private fun queryRounds(): RoundCursorWrapper {
        val sql = "SELECT "+ RoundDatabaseSchema.UserTable.Cols.PLAYERNAME + ", " +
                RoundDatabaseSchema.UserTable.Cols.PLAYERUUID + ", " + RoundDatabaseSchema.RoundTable.Cols.ROUNDUUID+
                ", " + RoundDatabaseSchema.RoundTable.Cols.DATE + ", " + RoundDatabaseSchema.RoundTable.Cols.TITLE+
                ", " + RoundDatabaseSchema.RoundTable.Cols.FILAS + ", " + RoundDatabaseSchema.RoundTable.Cols.COLUMNAS+
                ", " + RoundDatabaseSchema.RoundTable.Cols.BOARD+
                " " + "FROM "+ RoundDatabaseSchema.UserTable.NAME+ " AS p, " + RoundDatabaseSchema.RoundTable.NAME+
                " AS r " +  "WHERE " + "p."+
                RoundDatabaseSchema.UserTable.Cols.PLAYERUUID + "=" + "r."+
                RoundDatabaseSchema.RoundTable.Cols.PLAYERUUID+ ";"
        val cursor = db!!.rawQuery(sql, null)
        return RoundCursorWrapper(
            cursor
        )
    }


    override fun addRound(round: Round, callback: RoundRepository.BooleanCallback) {
        val values = getContentValues(round)
        val id = db!!.insert(RoundDatabaseSchema.RoundTable.NAME, null, values)
        callback.onResponse(id >= 0)

    }

    override fun updateRound(round: Round, callback: RoundRepository.BooleanCallback) {

        val values = getContentValues(round)
        val id = db!!.update(
            RoundDatabaseSchema.RoundTable.NAME, values, RoundDatabaseSchema.RoundTable.Cols.ROUNDUUID + " = ?",
            arrayOf(round.id)
        )
        callback.onResponse(id >= 0)

    }

    private fun getContentValues(round: Round): ContentValues {
        val values = ContentValues()
        values.put(RoundDatabaseSchema.RoundTable.Cols.PLAYERUUID, round.secondPlayerUUID)
        values.put(RoundDatabaseSchema.RoundTable.Cols.ROUNDUUID, round.id)
        values.put(RoundDatabaseSchema.RoundTable.Cols.DATE, round.date)
        values.put(RoundDatabaseSchema.RoundTable.Cols.TITLE, round.title)
        values.put(RoundDatabaseSchema.RoundTable.Cols.FILAS, round.filas)
        values.put(RoundDatabaseSchema.RoundTable.Cols.COLUMNAS, round.columnas)
        values.put(RoundDatabaseSchema.RoundTable.Cols.BOARD, round.board.tableroToString())
        return values
    }

    companion object {
        private val DATABASE_NAME = "er.db"
        private val DATABASE_VERSION = 1
    }

}


