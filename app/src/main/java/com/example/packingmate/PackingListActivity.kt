package com.example.packingmate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.db.AppDatabase
import com.example.packingmate.db.ListItemDao
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PackingListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PackingListAdapter
    private var tripId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packing_list)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerViewPackingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PackingListAdapter()
        recyclerView.adapter = adapter

        // TripActivity에서 넘겨준 tripId
        tripId = intent.getIntExtra("tripId", -1)

        if (tripId != -1) {
            lifecycleScope.launch {
                try {
                    val items = db.listItemDao().getItemsForTrip(tripId)
                    adapter.submitList(items)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@PackingListActivity, "데이터 로드 오류: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}
