package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.db.DBHelper
import com.example.packingmate.db.ListItem

class AIListActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: AIListAdapter
    lateinit var dbHelper: DBHelper

    lateinit var addButton: Button
    lateinit var saveButton: Button

    private var tripId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_list)

        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "AI추천 목록"

        // RecyclerView 연결
        recyclerView = findViewById(R.id.recycler_view)

        // DB
        dbHelper = DBHelper(this)
        val db = dbHelper.readableDatabase

        // tripId
        tripId = intent.getLongExtra("tripId", -1L)
        Log.d("DEBUG", "tripId = ${tripId}")

        // 리스트를 select해오는 부분
        val items = mutableListOf<RecyclerItem>()
        val cursor = db.rawQuery("SELECT * FROM listItem WHERE tripId = ?", arrayOf(tripId.toString()))

        while (cursor.moveToNext()) {
            val listItem = ListItem(
                itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId")),
                tripId = cursor.getInt(cursor.getColumnIndexOrThrow("tripId")),
                isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked")) == 1,
                itemName = cursor.getString(cursor.getColumnIndexOrThrow("itemName")),
                itemPlane = cursor.getString(cursor.getColumnIndexOrThrow("itemPlane")),
                isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1
            )
            items.add(RecyclerItem.ListItemType(listItem))
            Log.d("DEBUG_AIList", "items.size = ${items.size}")
        }

        cursor.close()
        db.close()

        // 어댑터 설정
        adapter = AIListAdapter(
            items,
            onItemDeleted = { item ->
                AlertDialog.Builder(this)
                    .setTitle("삭제")
                    .setMessage("'${item.itemName}'을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { dialog, _ ->
                        val db = dbHelper.writableDatabase
                        val stmt = db.compileStatement("DELETE FROM listItem WHERE itemId = ?")
                        stmt.bindLong(1, item.itemId.toLong())
                        stmt.executeUpdateDelete()
                        stmt.close()
                        db.close()

                        // 리스트 UI 제거
                        adapter.removeListItem(item)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            },
            onEditTextDeleted = { editTextId ->
                AlertDialog.Builder(this)
                    .setTitle("삭제")
                    .setMessage("입력 필드를 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { dialog, _ ->
                        adapter.removeEditText(editTextId)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 버튼 연결
        addButton = findViewById(R.id.addButton)
        saveButton = findViewById(R.id.saveButton)

        addButton.setOnClickListener {
            adapter.addEditText()
            // 새로 추가된 아이템으로 스크롤
            recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }

        saveButton.setOnClickListener {
            saveEditTextItems()

            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ListActivity::class.java))
            }, 1200)
        }
    }

    private fun saveEditTextItems() {
        val editTextValues = adapter.getEditTextValues()

        if (editTextValues.isEmpty()) return

        val db = dbHelper.writableDatabase
        val stmt = db.compileStatement(
            "INSERT INTO listItem (tripId, itemName, itemPlane, isChecked, isCustom) VALUES (?, ?, ?, ?, ?)"
        )

        val newItems = mutableListOf<RecyclerItem>()

        for (itemName in editTextValues) {
            stmt.bindLong(1, tripId)
            stmt.bindString(2, itemName)
            stmt.bindString(3, "사용자 추가") // CHECK 제약 조건에 포함돼야 함
            stmt.bindLong(4, 0)
            stmt.bindLong(5, 1)

            val newItemId = stmt.executeInsert()
            Log.d("DEBUG", "insert 결과: $newItemId")

            if (newItemId != -1L) {
                val newListItem = ListItem(
                    itemId = newItemId.toInt(),
                    tripId = tripId.toInt(),
                    itemName = itemName,
                    itemPlane = "사용자 추가",
                    isChecked = false,
                    isCustom = true
                )
                newItems.add(RecyclerItem.ListItemType(newListItem))
            }
        }

        stmt.close()
        db.close()

        // UI 갱신: 기존 EditText 제거 + insert된 항목 추가
        adapter.items.removeAll { it is RecyclerItem.EditTextType }
        adapter.items.addAll(newItems)
        adapter.notifyDataSetChanged()
    }


    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}