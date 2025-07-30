package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var tripList: List<TripItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        Log.d("ListActivity", "onCreate 시작됨")
        dbHelper = DBHelper(this)
        val tripListView = findViewById<ListView>(R.id.listView)

        //tripList = dbHelper.getAllTrips()
        tripList = dbHelper.getAllTrips() ?: emptyList()
        val tripTitles = tripList.map { it.title }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tripTitles)
        tripListView.adapter = adapter

        tripListView.setOnItemClickListener { _, _, position, _ ->
            val selectedTrip = tripList[position]

            val intent = Intent(this, ListDetailActivity::class.java)
            intent.putExtra("tripId", selectedTrip.tripId.toInt())
            intent.putExtra("tripTitle", selectedTrip.title)
            startActivity(intent)
        }
    }
}
