package com.example.packingmate

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat

class DBHelper(context: Context) : SQLiteOpenHelper(context, "packingMateDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE tripInfo (
                tripId INTEGER PRIMARY KEY AUTOINCREMENT,
                userGender INTEGER NOT NULL DEFAULT 1,
                userAge INTEGER NOT NULL,
                tripName CHAR(50) NOT NULL,
                tripStart TEXT,
                tripEnd TEXT,
                createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """)

        db.execSQL("""
            CREATE TABLE tripStyles (
                styleId INTEGER PRIMARY KEY AUTOINCREMENT,
                tripId INTEGER NOT NULL,
                styleName CHAR(10) NOT NULL CHECK(styleName IN ('도시 관광', '맛집 탐방', '쇼핑', '액티비티')),
                FOREIGN KEY(tripId) REFERENCES tripInfo(tripId)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tripStyles")
        db.execSQL("DROP TABLE IF EXISTS tripInfo")
        onCreate(db)
    }

    fun insertTripInfo(gender: Int, age: Int, trip: String, start: String, end: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("userGender", gender)
            put("userAge", age)
            put("tripName", trip)
            put("tripStart", start)
            put("tripEnd", end)
        }
        return db.insert("tripInfo", null, values)
    }

    fun insertTripStyle(tripId: Long, style: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tripId", tripId)
            put("styleName", style)
        }
        db.insert("tripStyles", null, values)
    }

    fun getAllTrips(): List<TripItem> {
        val db = readableDatabase
        val list = mutableListOf<TripItem>()
        val cursor = db.rawQuery("SELECT tripId, tripName, tripStart, tripEnd FROM tripInfo ORDER BY createdAt DESC", null)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val name = cursor.getString(1)
            val start = cursor.getString(2)
            val end = cursor.getString(3)
            val days = getTripDuration(start, end)
            list.add(TripItem(id, "$name ${days}일 여행"))
        }
        cursor.close()
        return list
    }

    private fun getTripDuration(start: String, end: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val diff = sdf.parse(end).time - sdf.parse(start).time
            (diff / (1000 * 60 * 60 * 24)).toInt() + 1
        } catch (e: Exception) {
            1
        }
    }
}
