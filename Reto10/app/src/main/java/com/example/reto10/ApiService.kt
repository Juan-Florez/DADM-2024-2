package com.example.reto10

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("resource/mgr2-njqc.json")
    suspend fun getData(
        @Query("nacionalidad") nacionalidad: String?,
        @Query("a_o_solicitud") anoSolicitud: String?,
        @Query("sexo") sexo: String?,
        @Query("\$limit") limit: Int,
        @Query("\$offset") offset: Int
    ): List<DataItem>
}