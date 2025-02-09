package com.example.reto11

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)