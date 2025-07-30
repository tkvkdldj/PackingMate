package com.example.packingmate.db

import androidx.room.*

@Dao
interface TripDao {
    @Query("SELECT * FROM listItems WHERE tripId = :tripId")
    fun getItemsByTrip(tripId: Int): List<ListItem>

    @Insert
    suspend fun insertTripInfo(tripInfo: TripInfo): Long

    @Insert
    suspend fun insertTripStyle(tripStyle: TripStyle): Long

    @Insert
    fun insertItem(item: ListItem)

    @Delete
    fun deleteItem(item: ListItem)

    @Insert
    suspend fun insertListItem(listItem: ListItem)

    @Query("SELECT * FROM tripInfo WHERE tripId = :tripId")
    suspend fun getTripInfoById(tripId: Int): TripInfo?

    @Query("SELECT * FROM tripStyles WHERE tripId = :tripId")
    suspend fun getTripStylesByTripId(tripId: Int): List<TripStyle>

}
