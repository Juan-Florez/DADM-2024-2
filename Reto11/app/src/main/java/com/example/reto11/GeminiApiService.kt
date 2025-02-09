package com.example.reto11

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    fun generateContent(
        @Query("key") apiKey: String, // API Key como parámetro de consulta
        @Body request: GeminiRequest
    ): Call<GeminiResponse>
}