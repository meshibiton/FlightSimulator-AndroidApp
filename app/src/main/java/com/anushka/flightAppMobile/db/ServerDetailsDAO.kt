package com.anushka.flightAppMobile.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ServerDetailsDAO {
    @Insert
    suspend fun insertServerDetails(server : ServerDetails) : Long

    @Update
    suspend fun updateServerDateConnection(server: ServerDetails) : Int

    @Delete
    suspend fun deleteServerDetails(server: ServerDetails) :Int

    @Query("SELECT * FROM server_db")
    fun getAllServersDetails() : LiveData<List<ServerDetails>>

    @Query("SELECT * FROM server_db ORDER BY date ASC LIMIT 1")
    suspend fun getLastServer() : ServerDetails

    @Query("SELECT COUNT(url) FROM server_db")
    suspend fun getNumRow() : Int
}