package com.example.packingmate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.R
import com.example.packingmate.data.db.DBHelper
import com.example.packingmate.data.db.ListItem
import com.example.packingmate.ui.adapter.LoadingAdapter

class LoadingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoadingAdapter
    private lateinit var dbHelper: DBHelper
    private var tripId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_list)

        tripId = intent.getIntExtra("tripId", -1)
        dbHelper = DBHelper(this)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LoadingAdapter(dbHelper) {
            loadListItems()  // 삭제 후 다시 리스트 로드
        }
        recyclerView.adapter = adapter

        loadListItems()
    }

    private fun loadListItems() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "listItem",
            null,
            "tripId = ?",
            arrayOf(tripId.toString()),
            null, null, null
        )

        val items = mutableListOf<ListItem>()
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

        adapter.submitList(items)
    }
}


