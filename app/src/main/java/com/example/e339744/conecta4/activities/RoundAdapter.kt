package com.example.e339744.conecta4.activities

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.views.ViewConecta4
import es.uam.eps.multij.Tablero

class RoundHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    override fun onClick(v: View?) {
        Toast.makeText(itemView.context, "Item ${idTextView.text} selected", Toast.LENGTH_SHORT).show()
    }

    private var idTextView: TextView
    private var boardViewConecta: ViewConecta4
    private var dateTextView: TextView
    private var estadoTextView : TextView
    private var nombresTextView : TextView
    private var items : RelativeLayout
    lateinit var round: Round

    init {
        idTextView = itemView.findViewById(R.id.list_item_id) as TextView
        boardViewConecta = itemView.findViewById(R.id.list_item_board) as ViewConecta4
        dateTextView = itemView.findViewById(R.id.list_item_date) as TextView
        estadoTextView = itemView.findViewById(R.id.list_item_estado) as TextView
        nombresTextView = itemView.findViewById(R.id.list_item_nombres) as TextView
        items = itemView.findViewById(R.id.itemsRound) as RelativeLayout
    }

    fun bindRound(round: Round, listener: (Round) -> Unit) {
        this.round = round
        idTextView.text = round.title
        boardViewConecta.setBoard(round.filas, round.columnas, round.board)
        boardViewConecta.invalidate()
        dateTextView.text = round.date.substring(0, 19)
        val nombre1 = round.secondPlayerName.split("@")[0]
        val nombre2 = round.firstPlayerName.split("@")[0]
        nombresTextView.text = "${nombre1} vs ${nombre2}"


        if (round.board.estado == Tablero.FINALIZADA) {
            if (round.board.turno == 0) {
                estadoTextView.text = "Victoria ${nombre1}!"
            } else {
                estadoTextView.text = "Victoria ${nombre2}!"
            }
        }else if(round.board.estado == Tablero.TABLAS){
                estadoTextView.text = "Partida en tablas!"
        }else if ((round.firstPlayerName == SettingsActivityConecta4.getPlayerName(itemView.context) && round.board.turno == 0)
            or (round.secondPlayerName == SettingsActivityConecta4.getPlayerName(itemView.context) && round.board.turno == 1)
        ) {
            estadoTextView.text = "Esperando movimiento rival"
            estadoTextView.textSize = 16F
        } else if (round.secondPlayerName == SettingsActivityConecta4.getPlayerName(itemView.context) &&
                round.firstPlayerName == "Random"){
            estadoTextView.text = "Esperando rival"
            estadoTextView.textSize = 16F
        } else if (round.secondPlayerName != SettingsActivityConecta4.getPlayerName(itemView.context) &&
            round.firstPlayerName == "Random"){
            estadoTextView.text = "Pulsa para unirte"
            estadoTextView.textSize = 16F
            idTextView.setOnClickListener { listener(round) }
            boardViewConecta.setOnClickListener { listener(round) }
            dateTextView.setOnClickListener { listener(round) }
            nombresTextView.setOnClickListener { listener(round) }
            items.setOnClickListener{listener(round)}
        }else{
            estadoTextView.text = ""
            idTextView.setOnClickListener { listener(round) }
            boardViewConecta.setOnClickListener { listener(round) }
            dateTextView.setOnClickListener { listener(round) }
            nombresTextView.setOnClickListener { listener(round) }
            items.setOnClickListener{listener(round)}
        }


    }

}

class RoundAdapter(var rounds: List<Round>, val listener: (Round) -> Unit) : RecyclerView.Adapter<RoundHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_round, parent, false)
        return RoundHolder(view)
    }

    override fun getItemCount(): Int = rounds.size

    override fun onBindViewHolder(holder: RoundHolder, position: Int) {
        holder.bindRound(rounds[position], listener)
    }


}