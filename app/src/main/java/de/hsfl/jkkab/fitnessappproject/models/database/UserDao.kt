package de.hsfl.jkkab.fitnessappproject.models.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insert(user: User?)

    @Update
    fun update(user: User?)

    @Query("DELETE FROM users_table")
    fun deleteAllUsers()

    @Delete
    fun delete(user: User?)

    @get:Query("SELECT * FROM users_table ")
    val allUsers: List<User>

    @get:Query("SELECT * FROM users_table WHERE id = 1")
    val user: LiveData<User>
}