package com.example.packingmate.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.*
import androidx.appcompat.app.AppCompatActivity
import com.example.packingmate.BuildConfig

import com.example.packingmate.R
import com.example.packingmate.data.db.DBHelper
import com.example.packingmate.data.repository.OpenAIRepository


class LoadingActivity : AppCompatActivity() {

    private val apiKey = BuildConfig.OPENAI_API_KEY
    private lateinit var openAIRepository: OpenAIRepository
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val tripId = intent.getLongExtra("tripId", -1L)
        if (tripId == -1L) {
            // tripId 없으면 에러 처리
            finish()
            return
        }

        openAIRepository = OpenAIRepository(this, apiKey)
        dbHelper = DBHelper(this)

        // GPT 호출하고 DB에 저장 후 화면 전환
        fetchAndSavePackingList(tripId)
    }

    private fun fetchAndSavePackingList(tripId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1) 여행 정보 가져오기 (DB에서)
                val trip = dbHelper.getTripById(tripId)
                if (trip == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoadingActivity, "여행 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    return@launch
                }

                // 2) 여행 정보 기반 프롬프트 만들기
                val prompt = "성별: ${trip.userGender}, 나이: ${trip.userAge}, 여행명: ${trip.tripName}, " +
                        "기간: ${trip.tripStart}부터 ${trip.tripEnd}까지 여행 준비물 리스트를 알려줘."

                // 3) GPT 호출해서 아이템 리스트 받아오기
                val items = openAIRepository.getPackingListFromGPT(prompt)

                // 4) DB에 아이템들 삽입
                val db = dbHelper.writableDatabase
                items.forEach { itemName ->
                    val values = ContentValues().apply {
                        put("tripId", tripId)
                        put("itemName", itemName)
                        put("itemPlane", "기내 필수")  // 필요시 바꾸기
                        put("isChecked", 0)
                        put("isCustom", 0)
                    }
                    db.insert("listItem", null, values)
                }

                // 5) 메인 스레드에서 화면 전환
                withContext(Dispatchers.Main) {
                    val aiIntent = Intent(this@LoadingActivity, AIListActivity::class.java)
                    aiIntent.putExtra("tripId", tripId)
                    startActivity(aiIntent)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoadingActivity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}



