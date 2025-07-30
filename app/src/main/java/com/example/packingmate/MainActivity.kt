package com.example.packingmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMakeList = findViewById<Button>(R.id.btn_make_list)
        val btnGoList = findViewById<Button>(R.id.btn_go_list)

        btnMakeList.setOnClickListener {
            startActivity(Intent(this, InputActivity::class.java))
        }

        btnGoList.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }
    }
}
