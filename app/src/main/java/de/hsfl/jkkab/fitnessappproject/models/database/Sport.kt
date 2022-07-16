package de.hsfl.jkkab.fitnessappproject.models.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sports_table")
class Sport (
    var name: String,
    var isTrack: Boolean,
    var isCardio: Boolean,
    var isSpeed: Boolean,
    var isSpeedAverage: Boolean,
    var isTime: Boolean,
    var isTimeElapsed: Boolean,
    var isDistance: Boolean,
    var isCalories:Boolean,
    var isHeartRate: Boolean,
    var isHeartRateAverage:Boolean){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}

