package com.example.e339744.conecta4.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.RoundRepository
import es.uam.eps.multij.*

import kotlinx.android.synthetic.main.fragment_round.*
import java.lang.Exception
import java.lang.RuntimeException

class RoundFragment : Fragment(), PartidaListener {
    private var FILAS = 6
    private var COLUMNAS = 7
    private lateinit var game: Partida
    private lateinit var round: Round
    private var nombre: String = ""
    var listener: OnRoundFragmentInteractionListener? = null

    interface OnRoundFragmentInteractionListener {
        fun onRoundUpdated(round: Round)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                round = Round.fromJSONString(it.getString(ARG_ROUND))
            }
        } catch (e: Exception) {
            Log.d("DEBUG", e.message)
            activity?.finish()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_round, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        round_title.text = "${round.title}"
        if (savedInstanceState != null) {
            round.board.stringToTablero(savedInstanceState.getString(BOARDSTRING))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(BOARDSTRING, round.board.tableroToString())
        super.onSaveInstanceState(outState)
    }


    companion object {
        val ARG_ROUND = "es.uam.eps.dadm.er20.round"
        val BOARDSTRING = "es.uam.eps.dadm.er20.boardstring"
        @JvmStatic
        fun newInstance(round: String) =
            RoundFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROUND, round)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        startRound()
    }

    override fun onResume() {
        super.onResume()
        board_view4enRaya.invalidate()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRoundFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnRoundFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    internal fun startRound() {
        val players = ArrayList<Jugador>()
        val localPlayer = JugadorConecta4(nombre)
        players.add(localPlayer)
        if (SettingsActivityConecta4.getLocal(context)) {
            val randomPlayer = JugadorAleatorio("CPU")
            players.add(randomPlayer)
        } else {
            val localPlayer2 = JugadorConecta4("Random")
            players.add(localPlayer2)

        }
        game = Partida(round.board, players)
        game.addObservador(this)
        localPlayer.setPartida(game)
        FILAS = round.filas
        COLUMNAS = round.columnas
        board_view4enRaya.setBoard(FILAS, COLUMNAS, round.board)
        board_view4enRaya.setOnPlayListener(localPlayer)

        registerListeners(localPlayer)
        if (game.tablero.estado == Tablero.EN_CURSO) {
            game.comenzar()
        }
    }

    override fun onCambioEnPartida(evento: Evento) {
        when (evento.tipo) {
            Evento.EVENTO_CAMBIO -> {
                board_view4enRaya.invalidate()
                listener?.onRoundUpdated(round)
                if (SettingsActivityConecta4.getLocal(context) == false) {
                    if ((round.firstPlayerName == SettingsActivityConecta4.getPlayerName(context) && round.board.turno == 0)
                        or (round.secondPlayerName == SettingsActivityConecta4.getPlayerName(context) && round.board.turno == 1)
                    ) {
                        startActivity(Intent(context, RoundListActivity::class.java))
                    }
                }
            }
            Evento.EVENTO_FIN -> {
                board_view4enRaya.invalidate()
                listener?.onRoundUpdated(round)
                //Toast.makeText(view!!.context, "Game over", Toast.LENGTH_SHORT).show()
                if (round.board.turno == 0) {
                    val alert = AlertDialogFragment()
                    val bundle = Bundle()
                    bundle.putString("NOMBREGANADOR", round.secondPlayerName)
                    alert.arguments = bundle
                    alert.show(activity?.supportFragmentManager, "ALERT_DIALOG")
                } else {
                    val alert = AlertDialogFragment()
                    val bundle = Bundle()
                    bundle.putString("NOMBREGANADOR", round.firstPlayerName)
                    alert.arguments = bundle
                    alert.show(activity?.supportFragmentManager, "ALERT_DIALOG")
                }
            }
        }
    }

    private fun registerListeners(local: JugadorConecta4) {
        val resetButton = view!!.findViewById(R.id.reset_round_fab) as FloatingActionButton

        if (SettingsActivityConecta4.getLocal(context) == false) {
            resetButton.visibility = View.GONE
        } else {

            resetButton.setOnClickListener(View.OnClickListener {
                if (round.board.estado != Tablero.EN_CURSO) {
                    Toast.makeText(view!!.context, R.string.round_already_finished, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                round.board.reset()
                startRound()
                board_view4enRaya.invalidate()
                listener?.onRoundUpdated(round)
                Toast.makeText(view!!.context, R.string.round_restarted, Toast.LENGTH_SHORT).show()
            })
        }
    }


    fun getRound(): Round {
        return round
    }

    fun setRound(cadena: String) {
        round.board.stringToTablero(cadena)
    }

    fun setNombre(nombre: String) {
        this.nombre = nombre
    }


}