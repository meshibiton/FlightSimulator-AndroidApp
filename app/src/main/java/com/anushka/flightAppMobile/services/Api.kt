package com.anushka.flightAppMobile.services

import com.anushka.flightAppMobile.models.Command
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {

    @GET("/screenshot")
    fun getImg(): Call<ResponseBody>

    @POST("api/command")
    fun addCommand(@Body newCommand: Command): Call<Command>
}