package com.example.packingmate.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "listItems",
    indices = [Index(value = ["tripId"])],
    foreignKeys = [
        ForeignKey(
            entity = TripInfo::class,
            parentColumns = ["tripId"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class ListItem(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val tripId: Int,
    val name: String,          // 리스트 아이템 이름
    val quantity: Int = 1,     // 수량
    val isPacked: Boolean = false // 체크 여부
)
