package com.example.packingmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListDetailActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //RecyclerView 연결
        recyclerView = findViewById(R.id.recycler_view)

        //어댑터 생성 -> 더미 데이터 넣기
        adapter = ListItemAdapter(ListItem.dummyList){ item, isChecked ->
            item.isChecked = isChecked
            //나중에 DB반영 로직은 여기에서
        }

        // RecyclerView에 어댑터와 레이아웃매니저 연결
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}