package io.github.dmitrytsyvtsyn.fluently.happening_list.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HappeningDao {

    @Query("select * from happening_table order by start_date")
    suspend fun fetch(): List<HappeningTable>

    @Query("select * from happening_table where id == :id")
    suspend fun fetch(id: Long): HappeningTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(table: HappeningTable)

    @Query("DELETE FROM happening_table WHERE id = :id")
    suspend fun delete(id: Long)

}