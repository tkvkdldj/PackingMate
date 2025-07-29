package com.example.packingmate

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        dbHelper = DBHelper(this)

        val tripListView = findViewById<ListView>(R.id.listView)
        val trips = dbHelper.getAllTrips()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, trips)
        tripListView.adapter = adapter
    }
}
