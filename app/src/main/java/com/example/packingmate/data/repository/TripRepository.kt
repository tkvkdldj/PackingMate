package com.example.packingmate.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.packingmate.data.db.SQLiteHelper
import com.example.packingmate.data.db.TripInfo

class TripRepository(private val context: Context) {
    private val dbHelper = SQLiteHelper(context)

    fun insertTripInfo(tripInfo: TripInfo): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userGender", tripInfo.userGender)
            put("userAge", tripInfo.userAge)
            put("tripName", tripInfo.tripName)
            put("tripStart", tripInfo.tripStart)
            put("tripEnd", tripInfo.tripEnd)
        }
        val tripId = db.insert("tripInfo", null, values).toInt()
        db.close()
        return tripId
    }

    fun insertTripStyle(tripId: Int, styleName: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tripId", tripId)
            put("styleName", styleName)
        }
        db.insert("tripStyles", null, values)
        db.close()
    }

    fun insertListItem(tripId: Int, name: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tripId", tripId)
            put("itemName", name)
            put("isChecked", 0)
            put("isCustom", 0)
        }
        db.insert("listItems", null, values)
        db.close()
    }
}
