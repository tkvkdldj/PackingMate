package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // TODO: GPT API 호출 로직 추가
        // GPT 응답 오면 → 다음 화면으로 전환

        // 예시: 3초 후 다음 화면 이동 (테스트용)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ListActivity::class.java))
            finish()
        }, 3000) // 3초
    }
}
