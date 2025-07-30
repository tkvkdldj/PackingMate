package com.example.packingmate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.Calendar

class InputActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "정보 입력"

        dbHelper = DBHelper(this)

        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)
        val ageEdit = findViewById<EditText>(R.id.editAge)
        val tripEdit = findViewById<EditText>(R.id.editTrip)
        val startEdit = findViewById<EditText>(R.id.editStartDate)
        val endEdit = findViewById<EditText>(R.id.editEndDate)

        val styleCity = findViewById<CheckBox>(R.id.checkCity)
        val styleFood = findViewById<CheckBox>(R.id.checkFood)
        val styleShopping = findViewById<CheckBox>(R.id.checkShopping)
        val styleActivity = findViewById<CheckBox>(R.id.checkActivity)

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        startEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateStr = "$year/${month + 1}/$dayOfMonth"
                    startEdit.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        endEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateStr = "$year/${month + 1}/$dayOfMonth"
                    endEdit.setText(dateStr)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSubmit.setOnClickListener {
            val gender = if (genderGroup.checkedRadioButtonId == R.id.radioMale) 1 else 0
            val age = ageEdit.text.toString().toInt()
            val tripName = tripEdit.text.toString()
            val startDate = startEdit.text.toString()
            val endDate = endEdit.text.toString()

            // name 제거됨
            val tripId = dbHelper.insertTripInfo(gender, age, tripName, startDate, endDate)

            val styles = listOfNotNull(
                if (styleCity.isChecked) "도시 관광" else null,
                if (styleFood.isChecked) "맛집 탐방" else null,
                if (styleShopping.isChecked) "쇼핑" else null,
                if (styleActivity.isChecked) "액티비티" else null
            )

            styles.forEach { style ->
                dbHelper.insertTripStyle(tripId, style)
            }

            Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("tripId", tripId)
            startActivity(intent)

            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.menu_list -> {
                startActivity(Intent(this, ListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
