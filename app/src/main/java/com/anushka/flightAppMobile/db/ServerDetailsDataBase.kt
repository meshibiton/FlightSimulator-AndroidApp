package com.anushka.flightAppMobile.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ServerDetails::class], version = 1)
abstract class ServerDetailsDataBase : RoomDatabase() {
    abstract val serverDetailsDAO : ServerDetailsDAO
    //to create singleton database
    companion object {
        @Volatile
        private var INSTANCE: ServerDetailsDataBase? = null
        fun getInstance(context: Context): ServerDetailsDataBase {
            synchronized(this) {
                var instance = INSTANCE
                return BuildInstance(instance, context)
            }
        }

        private fun BuildInstance(
            instance: ServerDetailsDataBase?,
            context: Context
        ): ServerDetailsDataBase {
            if (instance == null) {
                return Room.databaseBuilder(
                    context.applicationContext,
                    ServerDetailsDataBase::class.java, "server_db"
                ).build()
            }
            return instance
        }

    }
}

