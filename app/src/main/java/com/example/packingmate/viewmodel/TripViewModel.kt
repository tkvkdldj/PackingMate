package com.example.packingmate.viewmodel

import android.content.ContentValues
import androidx.lifecycle.ViewModel
import com.example.packingmate.data.db.DBHelper
import com.example.packingmate.data.repository.OpenAIRepository
import com.example.packingmate.data.db.TripInfo

class TripViewModel(
    private val openAiRepository: OpenAIRepository,
    private val dbHelper: DBHelper
) : ViewModel() {

    suspend fun generatePackingList(tripId: Long) {
        val tripInfo = dbHelper.getTripById(tripId)
            ?: throw IllegalStateException("Trip not found: $tripId")

        val prompt = buildPrompt(tripInfo, tripInfo.styles)
        val packingList = openAiRepository.getPackingListFromGPT(prompt)

        packingList.forEach { item ->
            dbHelper.insertListItem(tripId, item)
        }
    }


    private fun buildPrompt(tripInfo: TripInfo, tripStyles: List<String>): String {
        val styles = tripStyles.joinToString(", ")
        return """
        당신은 해외여행 짐 리스트 추천 전문가입니다.
        다음 여행 정보에 따라, 여행지의 기후, 계절, 여행 기간, 여행 스타일을 고려해
        필요한 짐 목록을 JSON 배열 형식으로 추천해 주세요.
        각 짐 아이템은 1~3 단어로 간결하게 작성해 주세요.

        여행지: ${tripInfo.tripName}
        여행 기간: ${tripInfo.tripStart} ~ ${tripInfo.tripEnd}
        여행 스타일: $styles

        예) ["여권", "기내 마스크", "충전기", "따뜻한 옷", "물놀이 용품"]
    """.trimIndent()
    }

    suspend fun submitTripAndGetPackingList(
        tripInfo: TripInfo,
        tripStyles: List<String>
    ): Long {
        // trip 저장
        val tripId = dbHelper.insertTripInfo(tripInfo)

        // 스타일 저장
        tripStyles.forEach { style ->
            dbHelper.insertTripStyle(tripId, style)
        }

        // GPT 호출
        val prompt = buildPrompt(tripInfo, tripStyles)
        val packingList = openAiRepository.getPackingListFromGPT(prompt)

        // 리스트 저장
        packingList.forEach { itemName ->
            dbHelper.insertListItem(tripId, itemName)
        }

        return tripId
    }

}
