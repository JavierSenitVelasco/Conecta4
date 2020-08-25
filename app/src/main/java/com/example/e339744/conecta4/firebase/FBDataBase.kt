package com.example.e339744.conecta4.firebase

import android.content.Context
import android.support.constraint.Constraints.TAG
import android.util.Log
import com.example.e339744.conecta4.database.RoundDatabaseSchema
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.RoundRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FBDataBase(context: Context) : RoundRepository {
    private val DATABASENAME = "partidas"
    lateinit var db: DatabaseReference
    var currentUser: FirebaseUser? = null




    override fun open() {
        db = FirebaseDatabase.getInstance().reference.child(DATABASENAME)
    }

    override fun close() {
    }

    override fun login(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(playername, password).addOnCompleteListener{ it ->
            if(it.isSuccessful) {
                currentUser = firebaseAuth.currentUser
                callback.onLogin(currentUser!!.uid)
            }else{
                callback.onError("Error al iniciar sesion")
            }
        }
    }

    override fun register(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(playername, password).addOnCompleteListener{ it ->
                if (it.isSuccessful) {
                    callback.onLogin(playername)
                } else {
                    callback.onError("Error al registrarse")
                }
            }
    }

    override fun getRounds(
        playerUUID: String,
        orderByField: String,
        group: String,
        callback: RoundRepository.RoundsCallback
    ) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var rounds = listOf<Round>()
                for (postSnapshot in dataSnapshot.children) {
                    val round = postSnapshot.getValue(Round::class.java)!!
                    if (isOpenOrIamIn(round))
                        rounds += round
                }
                callback.onResponse(rounds)
            }
        })
    }

    private fun isOpenOrIamIn(round: Round): Boolean {
        currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            if (currentUser?.email == round.firstPlayerName || currentUser?.email == round.secondPlayerName || (round.firstPlayerUUID == "Random")){
                return true
            }
        }
        return false
    }


    override fun addRound(round: Round, callback: RoundRepository.BooleanCallback) {
        db.child(round.id).setValue(round).addOnCompleteListener{ it ->
            if (it.isSuccessful) {
                callback.onResponse(true)
            } else {
                callback.onResponse(false)
            }
        }
    }

    override fun updateRound(round: Round, callback: RoundRepository.BooleanCallback) {
       db.child(round.id).setValue(round).addOnCompleteListener{ it ->
            if (it.isSuccessful) {
                callback.onResponse(true)
            } else {
                callback.onResponse(false)
            }
        }
    }


    fun startListeningChanges(callback: RoundRepository.RoundsCallback) {
        db = FirebaseDatabase.getInstance().reference.child(DATABASENAME)
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                var rounds = listOf<Round>()
                for (postSnapshot in p0.children)
                    rounds += postSnapshot.getValue(Round::class.java)!!
                callback.onResponse(rounds)
            }
        })
    }

    fun startListeningBoardChanges(callback: RoundRepository.RoundsCallback) {
        ///
    }
}
