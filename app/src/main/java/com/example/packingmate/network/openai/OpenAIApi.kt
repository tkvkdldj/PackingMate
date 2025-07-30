package com.example.packingmate.network.openai

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIApi {
    @POST("v1/chat/completions")
    fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: GPTRequest
    ): Call<GPTResponse>
}