package io.github.dmitrytsyvtsyn.fluently.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HappeningDao {

    @Query("select * from happening_table where end_date > :startDate and start_date < :endDate order by start_date")
    suspend fun fetch(startDate: Long, endDate: Long): List<HappeningTable>

    @Query("select * from happening_table where id == :id")
    suspend fun fetch(id: Long): HappeningTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(table: HappeningTable)

    @Query("DELETE FROM happening_table WHERE id = :id")
    suspend fun delete(id: Long)

}