package com.example.packingmate

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.packingmate.db.DBHelper

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val tripId = intent.getLongExtra("tripId", -1L)

        // TODO: GPT API 호출 로직 추가
        // GPT 응답 오면 → 다음 화면으로 전환

        /* ai 생성리스트 더미 데이터 insert */
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        val dummyItems = listOf(
            "여권" to "기내 필수",
            "항공권" to "기내 필수",
            "보조 배터리" to "기내 필수"
        )

        dummyItems.forEach { (name, plane) ->
            val values = ContentValues().apply {
                put("tripId", tripId)
                put("itemName", name)
                put("itemPlane", plane)
                put("isChecked", 0)
                put("isCustom", 0)
            }
            db.insert("listItem", null, values)
        }

        // 예시: 3초 후 다음 화면 이동 (테스트용)
        Handler(Looper.getMainLooper()).postDelayed({
            val aiIntent = Intent(this, AIListActivity::class.java)
            aiIntent.putExtra("tripId", tripId)
            startActivity(aiIntent)

            finish()
        }, 3000) // 3초
    }
}
