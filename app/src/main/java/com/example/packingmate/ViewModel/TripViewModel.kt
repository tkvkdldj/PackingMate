package com.example.packingmate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.packingmate.db.AppDatabase
import com.example.packingmate.db.ListItem
import com.example.packingmate.db.TripInfo
import com.example.packingmate.db.TripStyle
import com.example.packingmate.repository.OpenAIRepository
import kotlinx.coroutines.launch

class TripViewModel(
    private val db: AppDatabase,
    private val openAiRepository: OpenAIRepository
) : ViewModel() {

    private fun buildPrompt(tripInfo: TripInfo, tripStyles: List<TripStyle>): String {
        val styles = tripStyles.joinToString(", ") { it.styleName }
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
        tripStyles: List<TripStyle>
    ): Int {  // 반환 타입 명시(Int)
        val tripId = db.tripDao().insertTripInfo(tripInfo).toInt()

        // 스타일 저장
        tripStyles.forEach { style ->
            db.tripDao().insertTripStyle(style.copy(tripId = tripId))
        }

        // GPT 호출 등 다른 작업들 ...
        val prompt = buildPrompt(tripInfo, tripStyles)
        val packingList = openAiRepository.getPackingListFromGPT(prompt)

        packingList.forEach { itemName ->
            val listItem = ListItem(tripId = tripId, name = itemName)
            db.tripDao().insertListItem(listItem)
        }

        return tripId
    }

}
