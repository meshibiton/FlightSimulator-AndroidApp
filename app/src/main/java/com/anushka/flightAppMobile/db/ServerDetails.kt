package com.anushka.flightAppMobile.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_db")
data class ServerDetails (
    @PrimaryKey()
    var url : String,
    @ColumnInfo(name = "date")
    var date : Long
)