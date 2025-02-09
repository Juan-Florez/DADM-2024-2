package com.example.reto11

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tvChat: TextView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvChat = findViewById(R.id.tvChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            val userMessage = etMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                tvChat.append("\nTú: $userMessage")
                etMessage.text.clear()
                sendMessageToGemini(userMessage)
            }
        }
    }

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    private fun sendMessageToGemini(userMessage: String) {
        val apiKey = getApiKey()
        val contents = listOf(Content(parts = listOf(Part(text = userMessage))))
        val request = GeminiRequest(contents = contents)

        RetrofitClient.instance.generateContent(apiKey, request).enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                if (response.isSuccessful) {
                    val botMessage = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    tvChat.append("\nBot: $botMessage")
                } else {
                    tvChat.append("\nError: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                tvChat.append("\nError: ${t.message}")
            }
        })
    }
}