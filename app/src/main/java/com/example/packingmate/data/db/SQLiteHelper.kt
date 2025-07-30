package com.example.packingmate.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//다른 팀원의 것으로 새로 연결해야함
class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, "packingMateDB", null,9){
    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}