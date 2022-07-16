package de.hsfl.jkkab.fitnessappproject.models.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SportDao {
    @Insert
    fun insert(sport: Sport?)

    @Update
    fun update(sport: Sport?)

    @Query("DELETE FROM sports_table")
    fun deleteAllSports()

    @Delete
    fun delete(sport: Sport?)

    @get:Query("SELECT * FROM sports_table ")
    val allSports: List<Sport>

    @get:Query("SELECT * FROM sports_table WHERE id = 1")
    val sport: LiveData<Sport>
}