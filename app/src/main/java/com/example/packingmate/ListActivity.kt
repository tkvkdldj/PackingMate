package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var tripList: List<TripItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        dbHelper = DBHelper(this)
        val tripListView = findViewById<ListView>(R.id.listView)

        tripList = dbHelper.getAllTrips()
        val tripTitles = tripList.map { it.title }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tripTitles)
        tripListView.adapter = adapter

        tripListView.setOnItemClickListener { _, _, position, _ ->
            val selectedTrip = tripList[position]
            //val intent = Intent(this, ConfirmedListActivity::class.java)
            intent.putExtra("tripId", selectedTrip.tripId)
            startActivity(intent)
        }
    }
}
