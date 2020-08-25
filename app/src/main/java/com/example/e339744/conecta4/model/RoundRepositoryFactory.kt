package com.example.e339744.conecta4.model

import android.content.Context
import com.example.e339744.conecta4.R.id.switch_local
import com.example.e339744.conecta4.activities.SettingsActivityConecta4
import com.example.e339744.conecta4.database.DataBaseConecta4
import com.example.e339744.conecta4.firebase.FBDataBase
import kotlinx.android.synthetic.main.activity_loginconecta4.*
import java.lang.Exception

object RoundRepositoryFactory{
    private var LOCAL = false
    fun createRepository(context: Context): RoundRepository?{

        LOCAL = SettingsActivityConecta4.getLocal(context)
        val repository: RoundRepository
        repository = if (LOCAL) DataBaseConecta4(context) else FBDataBase(context)
        try{
            repository.open()
        }catch (e: Exception){
            return null
        }
        return repository
    }
}