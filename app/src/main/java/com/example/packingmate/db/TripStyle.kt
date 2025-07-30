package com.example.packingmate.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "tripStyles",
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
data class TripStyle(
    @PrimaryKey(autoGenerate = true) val styleId: Int = 0,
    val tripId: Int,
    val styleName: String
)
