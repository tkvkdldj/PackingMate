package com.example.packingmate

//여려 데이터를 끌고 다닐 모델 클래스
/*
CREATE TABLE listItem (
   itemId INTEGER PRIMARY KEY AUTOINCREMENT,
   tripId INTEGER NOT NULL,
   itemName CHAR(50) NOT NULL,
   itemPlane CHAR(10) NOT NULL CHECK (itemPlane IN ('기내 필수','기내 권장','기내/위탁 가능','위탁 필수')),
   isCustom INTEGER NOT NULL DEFAULT 0,
   FOREIGN KEY (tripId) REFERENCES tripInfo(tripId)
 );
*/
data class ListItem(
    val itemId : Int,
    val tripId: Int,
    val itemName: String,
    val itemPlane: String,
    var isChecked: Boolean,
    val isCustom: Boolean
)