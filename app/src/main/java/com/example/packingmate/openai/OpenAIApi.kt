package com.example.packingmate.openai

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Header

interface OpenAIApi {
    @POST("v1/chat/completions")
    fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: GPTRequest
    ): Call<GPTResponse>
}