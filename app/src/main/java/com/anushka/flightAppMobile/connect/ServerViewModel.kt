package com.anushka.flightAppMobile.connect

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anushka.flightAppMobile.ControlPanel
import com.anushka.flightAppMobile.Event
import com.anushka.flightAppMobile.simulatorcontrol.controlsimulatorActivity
import com.anushka.flightAppMobile.db.ServerDetails
import com.anushka.flightAppMobile.db.ServerDetailsRepository
import com.anushka.flightAppMobile.services.Api
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_control_panel.*
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



class ServerViewModel(private val repository: ServerDetailsRepository, private  val context: Context) :
    ViewModel(), Observable, ControlPanel.urlInterface {

    val servers = repository.servers
    private var prevUrl = ""
    @Bindable
    val inputUrlServer = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage




    override fun getUrl (): String{
        return prevUrl.toString()
    }
    fun saveOrUpdate() {
        if(inputUrlServer.value == null){
            statusMessage.value =
                Event("Please enter url server")
        } else {

            val list = servers.value
            val url = inputUrlServer.value!!
            val date = (System.currentTimeMillis()/1000)
            val currentsDetails = ServerDetails(url, date)
            if(serverExsistInDB(inputUrlServer.value!!)){
                update(currentsDetails)
            } else if(list?.size!! < 5){
                insert(currentsDetails)
                prevUrl = inputUrlServer.value.toString()
                inputUrlServer.value = null
            } else if(list.size >=5){
                deleteLast()
                prevUrl = inputUrlServer.value.toString()
                insert(currentsDetails)
            }
          //  checkIfConnect()
            val bool = work()
//            if(bool){
//                var intent = Intent(context, ControlPanel::class.java)
//       //     intent.putExtra("url", null)
//                intent.putExtra("url", inputUrlServer.value)
//                context.startActivity(intent)
//            }

//            var intent = Intent(context, ControlPanel::class.java)
//       //     intent.putExtra("url", null)
//            intent.putExtra("url", inputUrlServer.value)
//            context.startActivity(intent)
            //Todo try to connect
        }

    }

    fun insert(serverDetails: ServerDetails) = viewModelScope.launch {
        val newRowId = repository.insert(serverDetails)
        if(newRowId > -1){
            statusMessage.value =
                Event("Server Inserted Successfully")
        } else {
            statusMessage.value =
                Event("Error Occurred")
        }
    }

    fun update(serverDetails: ServerDetails) = viewModelScope.launch {
        val noOfRows = repository.update(serverDetails)
        if(noOfRows > 0){
            inputUrlServer.value = null
            statusMessage.value =
                Event("Row Updated Successfully")
        } else {
            statusMessage.value =
                Event("Error Occurred")
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
            statusMessage.value =
                Event("Error Occurred in delete")
        }
    }

    fun initServer(serverDetails: ServerDetails){
        inputUrlServer.value = serverDetails.url
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

//    private fun checkIfConnect() : Boolean{
//        var checkConnect = false
//        val logger: HttpLoggingInterceptor =
//            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//        //create Okhttp Client
//        val okHttp= OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).addInterceptor(logger)
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//       try {
//           val retrofit = Retrofit.Builder()
//               .baseUrl(inputUrlServer.value)
//               .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build())
//               .build()
//           for(i in 1..3) {
//               checkConnect = cheakIfGetImage(retrofit)
//               if(checkConnect){
//                   break
//               }
//           }
//           Toast.makeText(context, "failed to connect, try again!", Toast.LENGTH_SHORT).show()
//       } catch (e: Exception){
//           Toast.makeText(context, "failed to connect, try again!", Toast.LENGTH_SHORT).show()
//       }
//        return checkConnect
//    }

    private fun work() : Boolean{
        var url = inputUrlServer.value

        var intent = Intent(context, ControlPanel::class.java)
        var bool = false
        val logger:HttpLoggingInterceptor=HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        //create Okhttp Client
        val okHttp=OkHttpClient.Builder().callTimeout(10,TimeUnit.SECONDS).addInterceptor(logger)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(inputUrlServer.value)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build())
                .build()
            val api = retrofit.create(Api::class.java)
            val body = api.getImg().enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.code()==200){
                        val c = context

                        bool = true
                        Toast.makeText(c, "Connection ", Toast.LENGTH_SHORT).show()
                      //  var intent = Intent(context, ControlPanel::class.java)
       //     intent.putExtra("url", null)
                        intent.putExtra("url", url)
                        c.startActivity(intent)

                    }
                    else {
                        Toast.makeText(context, "failed to connect, try again!", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "failed to connect, try again!", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception){
            Toast.makeText(context, "failed to connect, try again!", Toast.LENGTH_SHORT).show()
        }
    return bool

    }
    private fun cheakIfGetImage(retrofit: Retrofit) : Boolean {
        var retrunBool = false
        val api = retrofit.create(Api::class.java)
        val context = this
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    retrunBool = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                retrunBool = false
            }
        })
        return retrunBool
    }

}