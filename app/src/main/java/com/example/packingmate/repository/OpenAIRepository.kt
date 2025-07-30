package com.example.packingmate.repository

import com.example.packingmate.openai.GPTRequest
import com.example.packingmate.openai.Message
import com.example.packingmate.openai.OpenAIApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenAIRepository(private val apiKey: String) {

    private val api: OpenAIApi = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIApi::class.java)

    // 코루틴에서 호출할 suspend 함수 예시
    suspend fun getPackingListFromGPT(prompt: String): List<String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val request = GPTRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    Message("system", "당신은 여행 준비 전문가입니다. " +
                            "아래 여행 정보에 맞는 필수 아이템 목록을 최대한 간결하고 명확하게 " +
                            "10개 이하 항목으로 한국어로 리스트 형태로 작성해주세요."),
                    Message("user", prompt)
                )
            )
            val response = api.getChatCompletion("Bearer $apiKey", request).execute()
            val content = response.body()?.choices?.firstOrNull()?.message?.content ?: ""
            // GPT가 응답한 텍스트에서 리스트 아이템만 추출 (예: 줄바꿈 기준 분리)
            content.lines()
                .map { it.trimStart(' ', '-', '•', '·') }
                .filter { it.isNotBlank() }
                .take(10) // 최대 10개
        }
    }
}

