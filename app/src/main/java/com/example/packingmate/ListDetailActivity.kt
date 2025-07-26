package com.example.packingmate

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListDetailActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ListItemAdapter

    private fun showDetailPopup(){
        val message = """
        여행지: 오사카
        여행 기간: 2025.08.01 ~ 2025.08.04
        기타 정보: ex) 여행 인원 2명
    """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(this).setTitle("여행 정보")
            .setMessage(message).setPositiveButton("확인",null).show()

    }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_info -> {
                showDetailPopup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}