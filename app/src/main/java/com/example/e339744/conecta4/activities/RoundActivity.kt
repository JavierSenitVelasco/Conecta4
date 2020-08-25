package com.example.e339744.conecta4.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.TableroConecta4
import es.uam.eps.multij.*
import android.widget.TextView
import com.example.e339744.conecta4.model.RoundRepository
import com.example.e339744.conecta4.model.RoundRepositoryFactory
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.activity_twopane.*
import kotlinx.android.synthetic.main.fragment_round_list.*


class RoundActivity : AppCompatActivity(), View.OnClickListener,
    RoundFragment.OnRoundFragmentInteractionListener {

    private val FILAS = 6
    private val COLUMNAS = 7
    val BOARDSTRING = "es.uam.eps.dadm.er8.grid"
    private lateinit var game: Partida
    private lateinit var board: TableroConecta4
    private lateinit var round: Round


    override fun onClick(view: View) {
        view.setBackgroundResource(R.drawable.green_button_48dp)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val fm = supportFragmentManager
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            val fragment = RoundFragment.newInstance(intent.getStringExtra(EXTRA_ROUND_ID))
            fm.executeTransaction { add(R.id.fragment_container, fragment) }


        }

        setSupportActionBar(my_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@RoundActivity, RoundListActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }



    override fun onSaveInstanceState(outState: Bundle?) {
        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragment_container) as RoundFragment
        outState?.putString(BOARDSTRING, fragment.getRound().board.tableroToString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        try {
            if (savedInstanceState?.getString(BOARDSTRING) != null) {
                val cadena = savedInstanceState.getString(BOARDSTRING)
                val fm = supportFragmentManager
                val fragment = fm.findFragmentById(R.id.fragment_container) as RoundFragment
                fragment.setRound(cadena)
            }
        } catch (e: ExcepcionJuego) {
            e.printStackTrace()
            Toast.makeText(this, "ExcepcionJuego Thrown", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onRoundUpdated(round: Round) {
            val repository = RoundRepositoryFactory.createRepository(this)
            val callback = object : RoundRepository.BooleanCallback {
                override fun onResponse(response: Boolean) {
                    if (response) {

                    } else
                        Snackbar.make(findViewById(R.id.title),
                            R.string.error_updating_round,
                            Snackbar.LENGTH_LONG).show()
                }
            }
            repository?.updateRound(round, callback)
    }
    private fun onRoundSelected(round: Round) {
        if (detail_fragment_container == null) {
            startActivity(RoundActivity.newIntent(this, round.toJSONString()))
        } else {
            supportFragmentManager.executeTransaction {
                replace(R.id.detail_fragment_container,
                    RoundFragment.newInstance(round.toJSONString())) }
        }
    }


    companion object {
        val EXTRA_ROUND_ID = "es.uam.eps.dadm.er8.round_id"
        fun newIntent(packageContext: Context, roundId: String): Intent {
            val intent = Intent(packageContext, RoundActivity::class.java)
            intent.putExtra(EXTRA_ROUND_ID, roundId)

            return intent
        }
    }

}
