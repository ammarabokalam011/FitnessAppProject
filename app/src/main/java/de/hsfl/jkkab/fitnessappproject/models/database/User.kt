package de.hsfl.jkkab.fitnessappproject.models.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
class User(
    var name: String,
    var email: String,
    var age: Int,
    var isMale: Boolean,
    var weight: Float,
    var height: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}