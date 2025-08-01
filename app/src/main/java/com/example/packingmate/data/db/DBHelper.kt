package com.example.packingmate.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat

class DBHelper(context: Context) : SQLiteOpenHelper(context, "packingMateDB", null, 9) {
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

        db.execSQL("""CREATE TABLE listItem (
                        itemId INTEGER PRIMARY KEY AUTOINCREMENT,    
                        tripId INTEGER NOT NULL,
                        itemName CHAR(50) NOT NULL,                      
                        itemPlane CHAR(10) NOT NULL CHECK (itemPlane IN 
                        ('기내 필수','기내 권장','기내/위탁 가능','위탁 필수','사용자 추가')),
                        isChecked INTEGER NOT NULL DEFAULT 0,                    
                        isCustom INTEGER NOT NULL DEFAULT 0,  
                        FOREIGN KEY (tripId) REFERENCES tripInfo(tripId)
                    );""".trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tripStyles")
        db.execSQL("DROP TABLE IF EXISTS tripInfo")
        db.execSQL("DROP TABLE IF EXISTS listItem")
        onCreate(db)
    }

    fun insertTripInfo(gender: Int, age: Int, trip: String, start: String, end: String): Long {
        if (trip.isBlank() || age <= 0) {
            Log.e("DBHelper", "insertTripInfo failed: invalid data. trip='$trip', age=$age")
            return -1
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put("userGender", gender)
            put("userAge", age)
            put("tripName", trip)
            put("tripStart", start)
            put("tripEnd", end)
        }

        val rowId = db.insert("tripInfo", null, values)

        if (rowId == -1L) {
            Log.e("DBHelper", "insertTripInfo failed. values=$values")
        } else {
            Log.d("DBHelper", "insertTripInfo succeeded: rowId=$rowId, values=$values")
        }

        return rowId
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

    fun getListItemsByTrip(context: Context, tripId: Int): List<ListItem> {
        val dbHelper = SQLiteHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT itemId, tripId, itemName, itemPlane, isChecked, isCustom FROM listItem WHERE tripId = ?", arrayOf(tripId.toString()))
        val items = mutableListOf<ListItem>()

        while(cursor.moveToNext()) {
            items.add(
                ListItem(
                    itemId = cursor.getInt(0),
                    tripId = cursor.getInt(1),
                    itemName = cursor.getString(2),
                    itemPlane = cursor.getString(3),
                    isChecked = cursor.getInt(4) == 1,
                    isCustom = cursor.getInt(5) == 1
                )
            )
        }
        cursor.close()
        return items
    }

    fun getTripById(tripId: Long): TripInfo? {
        val db = readableDatabase
        val cursor = db.query(
            "tripInfo",
            null,
            "tripId = ?",
            arrayOf(tripId.toString()),
            null, null, null
        )
        var tripInfo: TripInfo? = null
        if (cursor.moveToFirst()) {
            tripInfo = TripInfo(
                userGender = cursor.getInt(cursor.getColumnIndexOrThrow("userGender")),
                userAge = cursor.getInt(cursor.getColumnIndexOrThrow("userAge")),
                tripName = cursor.getString(cursor.getColumnIndexOrThrow("tripName")),
                tripStart = cursor.getString(cursor.getColumnIndexOrThrow("tripStart")),
                tripEnd = cursor.getString(cursor.getColumnIndexOrThrow("tripEnd")),
                styles = getTripStyles(tripId.toInt())
            )
        }
        cursor.close()
        return tripInfo
    }

    fun getItemsByTripId(tripId: Long): List<ListItem> {
        val items = mutableListOf<ListItem>()
        val db = readableDatabase
        val cursor = db.query(
            "listItem",  // 실제 테이블명으로 변경
            null,
            "tripId = ?",
            arrayOf(tripId.toString()),
            null, null, null
        )
        while (cursor.moveToNext()) {
            val item = ListItem(
                itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId")),
                tripId = cursor.getInt(cursor.getColumnIndexOrThrow("tripId")),
                itemName = cursor.getString(cursor.getColumnIndexOrThrow("itemName")),
                itemPlane = cursor.getString(cursor.getColumnIndexOrThrow("itemPlane")),
                isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked")) == 1,
                isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1
            )
            items.add(item)
        }
        cursor.close()
        return items
    }

    fun insertTripInfo(tripInfo: TripInfo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tripName", tripInfo.tripName)
            put("tripStart", tripInfo.tripStart)
            put("tripEnd", tripInfo.tripEnd)
        }
        return db.insert("tripInfo", null, values) // Long 그대로 반환
    }

    fun insertTripStyle(tripId: Long, style: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tripId", tripId)
            put("styleName", style)
        }
        db.insert("tripStyles", null, values)
    }

    fun insertListItem(tripId: Long, itemName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tripId", tripId)
            put("itemName", itemName)
            put("isChecked", 0)
            put("isCustom", 0)
        }
        db.insert("listItem", null, values)
    }

    fun getTripInfo(tripId: Int): TripInfo {
        val db = readableDatabase

        val cursor = db.query(
            "tripInfo",
            null,
            "tripId = ?",
            arrayOf(tripId.toString()),
            null, null, null
        )

        var tripInfo: TripInfo? = null
        if (cursor.moveToFirst()) {
            val userGender = cursor.getInt(cursor.getColumnIndexOrThrow("userGender"))
            val userAge = cursor.getInt(cursor.getColumnIndexOrThrow("userAge"))
            val tripName = cursor.getString(cursor.getColumnIndexOrThrow("tripName"))
            val tripStart = cursor.getString(cursor.getColumnIndexOrThrow("tripStart"))
            val tripEnd = cursor.getString(cursor.getColumnIndexOrThrow("tripEnd"))
            cursor.close()

            val styles = getTripStyles(tripId)  // 스타일 리스트 따로 불러오기

            tripInfo = TripInfo(userGender, userAge, tripName, tripStart, tripEnd, styles)
        } else {
            cursor.close()
            throw Exception("TripInfo not found for tripId: $tripId")
        }

        return tripInfo
    }


    fun getTripStyles(tripId: Int): List<String> {
        val db = readableDatabase
        val cursor = db.query(
            "tripStyles",
            arrayOf("styleName"),
            "tripId = ?",
            arrayOf(tripId.toString()),
            null, null, null
        )

        val styles = mutableListOf<String>()
        while (cursor.moveToNext()) {
            styles.add(cursor.getString(cursor.getColumnIndexOrThrow("styleName")))
        }
        cursor.close()
        return styles
    }


}
