package com.example.packingmate

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AIListActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: AIListAdapter
    lateinit var dbHelper: DBHelper
    lateinit var  addButton : Button
    lateinit var  saveButton: Button

    private var tripId: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ai_list)

        //툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "AI추천 목록"

        //RecyclerView 연결
        recyclerView = findViewById(R.id.recycler_view)

        //DB
        dbHelper = DBHelper(this)
        val db = dbHelper.readableDatabase

        //tripId
        tripId = intent.getLongExtra("tripId", -1L)
        Log.d("DEBUG","tripId = ${tripId}")

        /* 리스트를 select해오는 부분 */
        val itemList = mutableListOf<ListItem>()
        val cursor = db.rawQuery("SELECT * FROM listItem WHERE tripId = ?", arrayOf(tripId.toString()))

        while (cursor.moveToNext()) {
            val item = ListItem(
                itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId")),
                tripId = cursor.getInt(cursor.getColumnIndexOrThrow("tripId")),
                isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked")) == 1,
                itemName = cursor.getString(cursor.getColumnIndexOrThrow("itemName")),
                itemPlane = cursor.getString(cursor.getColumnIndexOrThrow("itemPlane")),
                isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1
            )
            itemList.add(item)
            Log.d("DEBUG_AIList","itemList.size = ${itemList.size}")
        }

        cursor.close()
        db.close()

        adapter = AIListAdapter(itemList.toMutableList()){ item ->
            AlertDialog.Builder(this).setTitle("삭제").setMessage("'${item.itemName}'을 삭제하시겠습니까?")
                .setPositiveButton("삭제"){ dialog, _ ->
                    val db = dbHelper.writableDatabase
                    val stmt = db.compileStatement("DELETE FROM listItem WHERE itemId = ?")
                    stmt.bindLong(1, item.itemId.toLong())
                    stmt.executeUpdateDelete()
                    stmt.close()
                    db.close()

                    //리스트 ui 제거
                    val position = adapter.aiItemList.indexOf(item)
                    adapter.aiItemList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
                .setNegativeButton("취소", null)
                .show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //추가 및 저장 버튼 연결
        addButton = findViewById(R.id.addButton)
        saveButton = findViewById(R.id.saveButton)



    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

}