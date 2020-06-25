package com.anushka.flightAppMobile.connect

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anushka.flightAppMobile.db.ServerDetailsRepository
import java.lang.IllegalArgumentException

class ServerViewModelFactory(private val repository: ServerDetailsRepository, private val context: Context)
    :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
     if(modelClass.isAssignableFrom(ServerViewModel::class.java)){
         return ServerViewModel(repository, context) as T
     }
        throw IllegalArgumentException("Unknown View Model class")
    }

}