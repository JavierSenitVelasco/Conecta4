package com.example.e339744.conecta4.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.example.e339744.conecta4.R
import com.example.e339744.conecta4.model.Round
import com.example.e339744.conecta4.model.RoundRepository
import com.example.e339744.conecta4.model.RoundRepositoryFactory
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.activity_twopane.*
import kotlinx.android.synthetic.main.fragment_round_list.*
import java.lang.Exception

class RoundListActivity() : AppCompatActivity(),
    RoundListFragment.OnRoundListFragmentInteractionListener,
    RoundFragment.OnRoundFragmentInteractionListener {


    override fun onRoundUpdated(round: Round) {
        val repository = RoundRepositoryFactory.createRepository(this)
        val callback = object : RoundRepository.BooleanCallback {
            override fun onResponse(response: Boolean) {
                if (response) {
                    recyclerView.update(
                        SettingsActivityConecta4.getPlayerUUID(baseContext),
                        { round -> onRoundSelected(round) }
                    )
                } else
                    Snackbar.make(findViewById(R.id.title),
                        R.string.error_updating_round,
                        Snackbar.LENGTH_LONG).show()
            }
        }
        repository?.updateRound(round, callback)
    }

    override fun onRoundSelected(round: Round) {
        if (detail_fragment_container == null) {
            if(SettingsActivityConecta4.getLocal(this) == false){
                if(round.secondPlayerName != SettingsActivityConecta4.getPlayerName(this)) {
                    round.firstPlayerName = SettingsActivityConecta4.getPlayerName(this)
                    round.firstPlayerUUID = SettingsActivityConecta4.getPlayerUUID(this)
                }
            }
            startActivity(RoundActivity.newIntent(this, round.toJSONString()))
        } else {
            supportFragmentManager.executeTransaction {
                replace(R.id.detail_fragment_container,
                    RoundFragment.newInstance(round.toJSONString())) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_detail)


        val fm = supportFragmentManager
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            val rlf = RoundListFragment()
            var bundle = Bundle()
            rlf.arguments = bundle
            fm.executeTransaction { add(R.id.fragment_container, rlf) }
        }

        try {
            val aux = findViewById(R.id.detail_fragment_container) as FrameLayout
            setSupportActionBar(my_toolbar2)
        } catch (e: Exception) {
            setSupportActionBar(my_toolbar)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false)
    }



    override fun onNewRoundAdded() {
        val round = Round(SettingsActivityConecta4.getBoardFilas(this).toInt(), SettingsActivityConecta4.getBoardColumnas(this).toInt())
        round.firstPlayerName = "Random"
        round.firstPlayerUUID = "Random"
        round.secondPlayerName = SettingsActivityConecta4.getPlayerName(this)
        round.secondPlayerUUID = SettingsActivityConecta4.getPlayerUUID(this)
        val repository = RoundRepositoryFactory.createRepository(this)
        val callback = object : RoundRepository.BooleanCallback {
            override fun onResponse(response: Boolean) {
                if (response == false)
                    Snackbar.make(findViewById(R.id.recyclerView),
                        R.string.error_adding_round, Snackbar.LENGTH_LONG).show()
                else {
                    Snackbar.make(findViewById(R.id.recyclerView),
                        "New " + round.title + " added", Snackbar.LENGTH_LONG).show()
                    val fragmentManager = supportFragmentManager
                    val roundListFragment =
                        fragmentManager.findFragmentById(R.id.fragment_container)
                                as RoundListFragment
                    roundListFragment.recyclerView.update(
                        SettingsActivityConecta4.getPlayerUUID(baseContext),
                        { round -> onRoundSelected(round) }
                    )
                }
            }
        }
        repository?.addRound(round, callback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity, menu)
        return true
    }

    override fun onPreferenceSelected() {
        startActivity(Intent(this, SettingsActivityConecta4::class.java))
    }


    private fun startRound(round: Round) {
        val fm = supportFragmentManager
        if (findViewById<View>(R.id.detail_fragment_container) == null) {
            val intent = RoundActivity.newIntent(baseContext, round.toJSONString())
            startActivity(intent)
        } else {
            fm.executeTransaction { replace(R.id.detail_fragment_container,
                RoundFragment.newInstance(round.toJSONString())) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@RoundListActivity, LoginActivityConecta4::class.java)
                val repository = RoundRepositoryFactory.createRepository(this)
                repository?.close()
                startActivity(intent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

}