package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListDetailActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ListItemAdapter
    lateinit var dbHelper: DBHelper
    lateinit var tripTitle : String

    //intent에서 받아올 tripId
    private var tripId: Int = -1

    private fun showDetailPopup(){
       val tripInfo = TripInfo.getTripInfo(this, tripId) //이것도 아래 tripId랑 한꺼번에 통일 시켜야함

        if(tripInfo == null){
            AlertDialog.Builder(this).setTitle("오류")
                .setMessage("여행 정보를 불러올 수 없습니다.")
                .setPositiveButton("확인", null).show()
            return
        }

        val gender : String
        if(tripInfo.userGender == 0){
            gender = "여성"
        }
        else{
            gender = "남성"
        }

        val message = """
                        성별 : ${gender}
                        연령 : ${tripInfo.userAge}
                        여행지: ${tripInfo.tripName}
                        여행 기간: ${tripInfo.tripStart} ~ ${tripInfo.tripEnd}
                        여행 성향 : ${tripInfo.styles.joinToString(", ")}
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

        val extras = intent.extras
        if (extras == null) {
            Log.e("DEBUG", "intent extras is NULL")
        } else {
            for (key in extras.keySet()) {
                Log.d("DEBUG", "Intent extra: $key = ${extras.get(key)}")
            }
        }

        tripTitle = intent.getStringExtra("tripTitle") ?: "제목 없음"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title=tripTitle

        //tripId intent로 받아옴
        tripId = intent.getIntExtra("tripId", -1)
        if (tripId == -1) {
            Log.e("ListDetailActivity", "tripId is missing!")
            finish()
        }

        //RecyclerView 연결
        recyclerView = findViewById(R.id.recycler_view)

        //DB
        dbHelper = DBHelper(this)
        val db = dbHelper.readableDatabase


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
            Log.d("DEBUG_ListDatail","itemList.size = ${itemList.size}")
        }

        cursor.close()
        db.close()

        /* 리스트 각 물품 체크 여부를 update하는 부분 */
        adapter = ListItemAdapter(itemList){ item, isChecked ->
            val writableDb = dbHelper.writableDatabase
            val stmt = writableDb.compileStatement("UPDATE listItem SET isChecked = ? WHERE itemId = ?")
            stmt.bindLong(1, if (isChecked) 1 else 0)
            stmt.bindLong(2, item.itemId.toLong())
            stmt.executeUpdateDelete()
            stmt.close()
            writableDb.close()
        }

        // RecyclerView에 어댑터와 레이아웃매니저 연결
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                true
            }
            R.id.menu_info -> {
                showDetailPopup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}