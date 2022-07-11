package de.hsfl.jkkab.fitnessappproject.models

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager.*
import android.util.Log
import androidx.lifecycle.LiveData
import de.hsfl.jkkab.fitnessappproject.models.database.User

class CaloriesCalculator {
    enum class SportActivity {
        BIKING, JOGGING, WINDSURFING, STAND_UP_PADDLING, SAILING
    }

    private val activityValues = arrayOf(
        intArrayOf(83, 98, 113, 128, 145),
        intArrayOf(113, 132, 152, 165, 195),
        intArrayOf(58, 69, 80, 89, 101),
        intArrayOf(81, 98, 113, 126, 141),
        intArrayOf(45, 56, 66, 76, 87)
    )
    private var caloriesPerSec = 0.0
    private var user: User? = null
    private var sportActivity = SportActivity.BIKING

    constructor(context: Context?) {
        val prefs: SharedPreferences = getDefaultSharedPreferences(context)
        val isMale = prefs.getString("gender", "female") == "male"
        val weight: Float = java.lang.Float.valueOf(prefs.getString("weight", "80"))
        val height: Float = java.lang.Float.valueOf(prefs.getString("height", "180"))
        val age: Int = prefs.getString("age", "35")!!.toInt()
        initCalc(isMale, weight, height, age, sportActivity)
    }

    constructor() {}
    constructor(user: User?) {
        if (user != null) {
            setUser(user)
        }
    }

    fun setUser(user: User) {
        this.user = user
        setActivity(sportActivity)
    }

    fun setActivity(sportActivity: SportActivity) {
        this.sportActivity = sportActivity
        if (user != null) {
            initCalc(
                user!!.isMale, user!!.weight, user!!.height.toFloat(), user!!.age,
                sportActivity
            )
        }
    }

    fun initCalc(
        isMale: Boolean,
        weight: Float,
        height: Float,
        age: Int,
        sportActivity: SportActivity
    ) {
        println(user?.name)
        Log.d("CaloriesController", "Recalculating with Activity: " + sportActivity.name)
        Log.d("CaloriesController", "$isMale $weight $height $age")
        val basicCalories: Double = if (isMale) {
            66.5 + 13.7 * weight + 5 * height - 6.8 * age
        } else {
            655 + 9.6 * weight + 1.8 * height - 4.7 * age
        }
        var weightIndex = 0
        if (weight in 65.0..74.0) {
            weightIndex = 1
        } else if (weight in 75.0..84.0) {
            weightIndex = 2
        } else if (weight in 85.0..94.0) {
            weightIndex = 3
        } else if (95 <= weight) {
            weightIndex = 4
        }
        val caloriesPerQoaH =
            basicCalories / 96.0 + activityValues[sportActivity.ordinal][weightIndex]
        caloriesPerSec = caloriesPerQoaH / 900.0
    }

    /**
     *
     * @param sec
     * @return kcal
     */
    fun getCalories(sec: Long): Double {
        return caloriesPerSec * sec
    }
}