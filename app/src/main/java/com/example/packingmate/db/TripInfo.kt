package com.example.packingmate.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "tripInfo")
data class TripInfo(
    @PrimaryKey(autoGenerate = true) val tripId: Int = 0,
    val userGender: Int = 1,   // 0: 여성, 1: 남성
    val userAge: Int,
    val tripName: String,
    val tripStart: String?,    // YYYY-MM-DD 형태로 저장 (nullable 가능)
    val tripEnd: String?,      // YYYY-MM-DD 형태로 저장 (nullable 가능)
    val createdAt: String      // ISO 8601 형식 날짜 문자열 (ex. 2025-07-29T15:30:00)
)

// 예시로 현재 시간을 ISO 8601 문자열로 만드는 함수
fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
