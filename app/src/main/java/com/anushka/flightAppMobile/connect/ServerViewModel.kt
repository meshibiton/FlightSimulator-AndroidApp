package com.anushka.flightAppMobile.connect

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anushka.flightAppMobile.ControlPanel
import com.anushka.flightAppMobile.db.ServerDetails
import com.anushka.flightAppMobile.db.ServerDetailsRepository
import com.anushka.flightAppMobile.services.Api
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit


/**
 * Server view model class - Logic of DB
 */
class ServerViewModel(private val repository: ServerDetailsRepository,
                      private  val context: Context) : ViewModel(), Observable{

    val servers = repository.servers
    @Bindable
    val inputUrlServer = MutableLiveData<String>()

//    val message : LiveData<Event<String>>
//        get() = statusMessage


    fun saveOrUpdate() {
        if(inputUrlServer.value == null || inputUrlServer.value!!.length == 0){
            Toast.makeText(context, "Please enter url server", Toast.LENGTH_SHORT).show()
        } else {

            val list = servers.value
            val url = inputUrlServer.value!!
            //get cuurent time
            val date = (System.currentTimeMillis()/1000)
            val currentsDetails = ServerDetails(url, date)
            //update the DB
            if(serverExsistInDB(inputUrlServer.value!!)){
                update(currentsDetails)
            } else if(list?.size!! < 5){
                insert(currentsDetails)
                inputUrlServer.value = null
            } else if(list.size >=5){
                deleteLast()
                insert(currentsDetails)
            }
            val bool = connectToServer()
        }

    }
    //insert new server to DB Room
    fun insert(serverDetails: ServerDetails) = viewModelScope.launch {
        val newRowId = repository.insert(serverDetails)
        if(newRowId > -1){
            Toast.makeText(context, "Server Inserted Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    fun update(serverDetails: ServerDetails) = viewModelScope.launch {
        val noOfRows = repository.update(serverDetails)
        if(noOfRows > 0){
            inputUrlServer.value = null
            Toast.makeText(context, "Row Updated Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    //check if url is exsist in serverslist
    private fun serverExsistInDB(urlInput : String) : Boolean {

        for(s in servers.value!!){
            if(s.url == urlInput){
                return true
            }
        }
        return false
    }

    fun deleteLast() = viewModelScope.launch {
        val lastServer = repository.lastServer()
        val noOfRowsDeleted = repository.deleteServer(lastServer)
        if(noOfRowsDeleted > 0){
            inputUrlServer.value = null
        } else {
            Toast.makeText(context, "Error Occurred in delete", Toast.LENGTH_SHORT).show()
        }
    }

    fun initServer(serverDetails: ServerDetails){
        inputUrlServer.value = serverDetails.url
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?){

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
    //try to connect to server and go to control panel activity
    private fun connectToServer(){
        val url = inputUrlServer.value
        val intent = Intent(context, ControlPanel::class.java)
        val logger:HttpLoggingInterceptor=HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        //create Okhttp Client
        val okHttp=OkHttpClient.Builder().callTimeout(10,TimeUnit.SECONDS).addInterceptor(logger)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        connectRetrofit(gson, intent, url, okHttp)
    }

    fun connectRetrofit(gson: Gson, intent: Intent, url: String?, okHttp: OkHttpClient.Builder) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(inputUrlServer.value)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build())
                .build()
            val api = retrofit.create(Api::class.java)
            val body = api.getImg().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    //if the connection succseed
                    if(response.code()==200){
                        val c = context
                        Toast.makeText(c, "Connection ", Toast.LENGTH_SHORT).show()
                        intent.putExtra("url", url)
                        c.startActivity(intent)
                    }
                    else {
                        Toast.makeText(context, "failed to connect, try again!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "failed to connect, try again!",
                        Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception){
            Toast.makeText(context, "failed to connect, try again!", Toast.LENGTH_SHORT).show()
        }
    }
}