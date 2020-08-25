package com.example.e339744.conecta4.activities

import android.graphics.Color
import android.graphics.Paint
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.RoundRepository
import com.example.e339744.conecta4.model.RoundRepositoryFactory
import com.example.e339744.conecta4.model.TableroConecta4


fun RecyclerView.update(userName: String, onClickListener: (Round) -> Unit) {
    val repository = RoundRepositoryFactory.createRepository(context)
    val roundsCallback = object : RoundRepository.RoundsCallback {
        override fun onResponse(rounds: List<Round>) {
            if (adapter == null)
                adapter = RoundAdapter(rounds, onClickListener)
            else {
                (adapter as RoundAdapter).rounds = rounds
                adapter!!.notifyDataSetChanged()
            }
        }
        override fun onError(error: String) {
        }
    }
    repository?.getRounds(userName, "", "", roundsCallback)
}

fun FragmentManager.executeTransaction(operations: (FragmentTransaction.() -> Unit)) {
    val transaction = beginTransaction()
    transaction.operations()
    transaction.commit()
}




fun Paint.setColor(board: TableroConecta4, i: Int, j: Int) {
    if(board.uMovimiento[0] == i && board.uMovimiento[1] == j){
        if (board.getTablero(i, j) == 1) {
            setColor(Color.parseColor("#AFFED6"))
        }else {
            setColor(Color.parseColor("#DECDFF"))
        }
    }else {
        if (board.getTablero(i, j) == 1) {
            setColor(Color.parseColor("#4DD497"))
        } else if (board.getTablero(i, j) == 0) {
            setColor(Color.parseColor("#FFFFFF"))
        } else {
            setColor(Color.parseColor("#9779D4"))
        }
    }

}