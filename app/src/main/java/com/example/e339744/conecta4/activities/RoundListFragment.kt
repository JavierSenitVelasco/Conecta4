package com.example.e339744.conecta4.activities

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import kotlinx.android.synthetic.main.fragment_round_list.*
import java.lang.RuntimeException
import android.widget.TextView
import com.example.e339744.conecta4.firebase.FBDataBase
import com.example.e339744.conecta4.model.RoundRepository
import com.example.e339744.conecta4.model.RoundRepositoryFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RoundListFragment() : Fragment() {
    var listener: OnRoundListFragmentInteractionListener? = null
    interface OnRoundListFragmentInteractionListener {
        fun onRoundSelected(round: Round)
        fun onPreferenceSelected()
        fun onNewRoundAdded()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(SettingsActivityConecta4.getLocal(context) == false) {
            startListeningChanges()
        }
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRoundListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnRoundListFragmentInteractionListener")
        }
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        recyclerView.update(SettingsActivityConecta4.getPlayerUUID(context!!))
        { round -> listener?.onRoundSelected(round) }
    }

    fun startListeningChanges() {
        val repository = RoundRepositoryFactory.createRepository(context!!) as FBDataBase
        val callback = object : RoundRepository.RoundsCallback {
            override fun onResponse(rounds: List<Round>) {
                for (round in rounds){
                    recyclerView.update(
                        SettingsActivityConecta4.getPlayerUUID(context),
                        { round -> listener?.onRoundSelected(round) }
                    )
                }
            }

            override fun onError(error: String) {
                Snackbar.make(view as View,
                    R.string.error_updating_round,
                    Snackbar.LENGTH_LONG).show()
            }


        }
        repository.startListeningChanges(callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_round_list, container, false)
        registerListeners(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            update(SettingsActivityConecta4.getPlayerUUID(context!!))
            { round -> listener?.onRoundSelected(round) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_item_settings -> {
                listener?.onPreferenceSelected()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun registerListeners(rootView :View) {
        val createButton = rootView.findViewById(R.id.create_round_fab) as FloatingActionButton
        createButton.setOnClickListener {
            listener?.onNewRoundAdded()
        }
    }


}