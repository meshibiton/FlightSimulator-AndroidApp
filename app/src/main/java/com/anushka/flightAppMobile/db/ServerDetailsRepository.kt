package com.anushka.flightAppMobile.db


class ServerDetailsRepository(private val dao : ServerDetailsDAO) {
    val servers = dao.getAllServersDetails()

    suspend fun insert(serverDetails: ServerDetails): Long {
        return dao.insertServerDetails(serverDetails)
    }
    suspend fun update(serverDetails: ServerDetails): Int {
        return dao.updateServerDateConnection(serverDetails)
    }

    suspend fun deleteServer(serverDetails: ServerDetails) :Int{
        return dao.deleteServerDetails(serverDetails)
    }
    suspend fun lastServer() : ServerDetails {
        return dao.getLastServer()
    }

    suspend fun numRow() :Int {
        return dao.getNumRow()
    }


}