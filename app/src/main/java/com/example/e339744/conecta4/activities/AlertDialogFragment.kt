package com.example.e339744.conecta4.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.RoundRepository
import com.example.e339744.conecta4.model.RoundRepositoryFactory
import kotlinx.android.synthetic.main.fragment_round_list.*

class AlertDialogFragment() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity as AppCompatActivity?
        val alertDialogBuilder = AlertDialog.Builder(getActivity())
        //val nombreGanador = bundle?.getString("NOMBREGANADOR") as String


        //alertDialogBuilder.setTitle("Partida finalizada - $nombreGanador gana")
        alertDialogBuilder.setTitle("Partida finalizada")
        alertDialogBuilder.setMessage(R.string.game_over_message)
        alertDialogBuilder.setPositiveButton("Si") { dialog, which ->
            val filas = SettingsActivityConecta4.getBoardFilas(getActivity() as Context).toInt()
            val columnas = SettingsActivityConecta4.getBoardColumnas(getActivity() as Context).toInt()
            val round = Round(filas, columnas)


            if (activity is RoundListActivity) {
                activity.onRoundUpdated(round)
            } else {
                activity?.finish()
            }
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("No",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (activity is RoundActivity) {
                        activity.finish()
                    }
                    dialog?.dismiss()
                }
            })
        return alertDialogBuilder.create()
    }
}