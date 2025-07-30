package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.packingmate.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import com.example.packingmate.db.TripInfo
import com.example.packingmate.db.TripStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.packingmate.db.AppDatabase
import com.example.packingmate.viewmodel.TripViewModelFactory
import com.example.packingmate.repository.OpenAIRepository

class TripActivity : AppCompatActivity() {

    private lateinit var tripViewModel: TripViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)

        val db = AppDatabase.getInstance(this)  // 싱글톤 인스턴스 가져오는 방법에 맞게 수정하세요
        val apiKey = BuildConfig.OPENAI_API_KEY
        val openAiRepository = OpenAIRepository(apiKey)

        val factory = TripViewModelFactory(db, openAiRepository)
        tripViewModel = ViewModelProvider(this, factory).get(TripViewModel::class.java)


        val editTripName = findViewById<EditText>(R.id.editTripName)
        val editUserAge = findViewById<EditText>(R.id.editUserAge)
        val radioGroupGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val editTripStart = findViewById<EditText>(R.id.editTripStart)
        val editTripEnd = findViewById<EditText>(R.id.editTripEnd)
        val checkStyleCityTour = findViewById<CheckBox>(R.id.checkStyleCityTour)
        val checkStyleHiking = findViewById<CheckBox>(R.id.checkStyleHiking)
        val checkStyleBeach = findViewById<CheckBox>(R.id.checkStyleBeach)
        val checkStyleCulture = findViewById<CheckBox>(R.id.checkStyleCulture)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val tripName = editTripName.text.toString().ifEmpty { "테스트 여행" }
            val userAge = editUserAge.text.toString().toIntOrNull() ?: 30

            val userGender = when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> 1
                R.id.radioFemale -> 2
                else -> 1
            }

            val tripStart = editTripStart.text.toString().ifEmpty { "2025-08-01" }
            val tripEnd = editTripEnd.text.toString().ifEmpty { "2025-08-10" }

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val createdAtString = sdf.format(Date())

            val tripInfo = TripInfo(
                tripId = 0,
                userGender = userGender,
                userAge = userAge,
                tripName = tripName,
                tripStart = tripStart,
                tripEnd = tripEnd,
                createdAt = createdAtString
            )

            val tripStyles = mutableListOf<TripStyle>()
            var styleIdCounter = 0

            if (checkStyleCityTour.isChecked) {
                tripStyles.add(TripStyle(styleIdCounter++, 0, "도시 관광"))
            }
            if (checkStyleHiking.isChecked) {
                tripStyles.add(TripStyle(styleIdCounter++, 0, "등산"))
            }
            if (checkStyleBeach.isChecked) {
                tripStyles.add(TripStyle(styleIdCounter++, 0, "해변"))
            }
            if (checkStyleCulture.isChecked) {
                tripStyles.add(TripStyle(styleIdCounter++, 0, "문화 체험"))
            }

            //로그
            Log.d("TripActivity", "tripInfo: $tripInfo, tripStyles: $tripStyles")

            lifecycleScope.launch {
                try {
                    val tripId = tripViewModel.submitTripAndGetPackingList(tripInfo, tripStyles)
                    Toast.makeText(this@TripActivity, "제출 성공 및 리스트 생성 완료", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@TripActivity, PackingListActivity::class.java)
                    intent.putExtra("tripId", tripId)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("TripActivity", "오류 발생", e)
                    Toast.makeText(this@TripActivity, "오류 발생: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

