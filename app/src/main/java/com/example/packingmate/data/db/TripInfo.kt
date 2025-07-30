package com.example.packingmate.data.db

import android.content.Context

data class TripInfo (
    val tripId: Int,    // 추가
    val userGender: Int,
    val userAge: Int,
    val tripName: String,
    val tripStart: String,
    val tripEnd: String,
    val styles: List<String>
){
    companion object {
        fun getTripInfo(context: Context, tripId: Int): TripInfo? {
            val dbHelper = SQLiteHelper(context)
            val db = dbHelper.readableDatabase

            val cursor = db.rawQuery(
                """
                SELECT ti.tripId, ti.userGender, ti.userAge, 
                       ti.tripName, ti.tripStart, ti.tripEnd,
                       ts.styleName
                FROM tripInfo ti
                LEFT JOIN tripStyles ts ON ti.tripId = ts.tripId
                WHERE ti.tripId = ?
                """.trimIndent(), arrayOf(tripId.toString())
            )

            var tripInfo: TripInfo? = null
            val styleList = mutableListOf<String>()

            if (cursor.moveToFirst()) {
                val id = cursor.getInt(0)
                val userGender = cursor.getInt(1)
                val userAge = cursor.getInt(2)
                val tripName = cursor.getString(3)
                val tripStart = cursor.getString(4)
                val tripEnd = cursor.getString(5)

                do {
                    val styleName = cursor.getString(6)
                    if (styleName != null) styleList.add(styleName)
                } while (cursor.moveToNext())

                tripInfo = TripInfo(
                    id, userGender, userAge,
                    tripName, tripStart, tripEnd,
                    styleList
                )
            }

            cursor.close()
            db.close()
            return tripInfo
        }
    }
}
