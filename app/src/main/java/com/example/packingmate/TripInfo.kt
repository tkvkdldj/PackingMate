package com.example.packingmate

import android.content.Context

data class TripInfo (
    val userGender : Int,
    val userAge : Int,
    val tripName : String,
    val tripStart : String,
    val tripEnd : String,
    val styles : List<String> //여행성향 복수 선택
    ){
        companion object{
            fun getTripInfo(context : Context, tripId : Int) : TripInfo? {
                val dbHelper = SQLiteHelper(context)
                val db = dbHelper.readableDatabase

                val cursor = db.rawQuery(
                    """
                    SELECT ti.userGender, ti.userAge, 
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
                    // 첫 행에서 trip 정보 추출
                    val userGender = cursor.getInt(0)
                    val userAge = cursor.getInt(1)
                    val tripName = cursor.getString(2)
                    val tripStart = cursor.getString(3)
                    val tripEnd = cursor.getString(4)

                    do {
                        val styleName = cursor.getString(5)
                        if (styleName != null) styleList.add(styleName)
                    } while (cursor.moveToNext())

                    tripInfo = TripInfo(
                        userGender, userAge,
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