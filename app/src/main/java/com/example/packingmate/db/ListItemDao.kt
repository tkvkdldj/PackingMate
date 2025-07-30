package com.example.packingmate.db

import androidx.room.*

@Dao
interface ListItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ListItem>)

    @Query("SELECT * FROM listItems WHERE tripId = :tripId")
    suspend fun getItemsForTrip(tripId: Int): List<ListItem>

}
