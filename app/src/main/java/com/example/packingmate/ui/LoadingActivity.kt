package com.example.packingmate.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.BuildConfig
import com.example.packingmate.R
import com.example.packingmate.data.db.DBHelper
import com.example.packingmate.data.db.ListItem
import com.example.packingmate.data.repository.OpenAIRepository

import com.example.packingmate.viewmodel.TripViewModel
import com.example.packingmate.viewmodel.TripViewModelFactory
import com.example.packingmate.ui.adapter.LoadingAdapter

class LoadingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoadingAdapter

    private val apiKey = BuildConfig.OPENAI_API_KEY
    private lateinit var openAIRepository: OpenAIRepository

    private lateinit var dbHelper: DBHelper
    private lateinit var viewModel: TripViewModel
    private var tripId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_list)

        val tripId = intent.getLongExtra("tripId", -1L)
        dbHelper = DBHelper(this)

        // GPT Repository + ViewModel
        val apiKey = BuildConfig.OPENAI_API_KEY
        val openAiRepository = OpenAIRepository(this, apiKey)
        val factory = TripViewModelFactory(openAiRepository, dbHelper)
        viewModel = ViewModelProvider(this, factory).get(TripViewModel::class.java)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LoadingAdapter(dbHelper) {
            loadListItems()
        }
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            val tripInfo = dbHelper.getTripById(tripId)
            if (tripInfo == null) {
                Log.e("LoadingActivity", "tripInfo 불러오기 실패: tripId=$tripId")
                Toast.makeText(this@LoadingActivity, "여행 정보를 불러올 수 없습니다.", Toast.LENGTH_LONG).show()
                return@launch
            }
            Log.d("LoadingActivity", "불러온 tripInfo: $tripInfo")
            viewModel.generatePackingList(tripId)
            loadListItems()
        }
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



