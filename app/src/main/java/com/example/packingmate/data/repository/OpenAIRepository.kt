package com.example.packingmate.data.repository

import android.content.Context
import com.example.packingmate.network.openai.GPTRequest
import com.example.packingmate.network.openai.Message
import com.example.packingmate.network.openai.OpenAIApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.example.packingmate.data.db.DBHelper
import com.example.packingmate.data.db.TripInfo
import com.example.packingmate.data.db.ListItem


class OpenAIRepository(context: Context, private val apiKey: String) {

    // DBHelper 초기화
    private val dbHelper: DBHelper = DBHelper(context)

    private val api: OpenAIApi = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIApi::class.java)

    // GPT 호출
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
            content.lines()
                .map { it.trimStart(' ', '-', '•', '·') }
                .filter { it.isNotBlank() }
                .take(10)
        }
    }

    // === DBHelper 연동 메서드 추가 ===
    fun getTripById(tripId: Long): TripInfo? {
        return dbHelper.getTripById(tripId)
    }

    fun insertTrip(trip: TripInfo): Long {
        return dbHelper.insertTripInfo(
            trip.userGender,
            trip.userAge,
            trip.tripName,
            trip.tripStart,
            trip.tripEnd
        )
    }

    fun getTripItems(tripId: Long): List<ListItem> {
        return dbHelper.getItemsByTripId(tripId)
    }
}

