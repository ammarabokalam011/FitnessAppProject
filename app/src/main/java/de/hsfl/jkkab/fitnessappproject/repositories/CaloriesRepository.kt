package de.hsfl.jkkab.fitnessappproject.repositories

import androidx.lifecycle.LiveData
import de.hsfl.jkkab.fitnessappproject.models.CaloriesCalculator
import de.hsfl.jkkab.fitnessappproject.models.database.OnUserUpdated
import de.hsfl.jkkab.fitnessappproject.models.database.User

class CaloriesRepository : OnUserUpdated {
    private val caloriesCalculator: CaloriesCalculator = CaloriesCalculator()
    fun changeSportActivity(sportActivity: CaloriesCalculator.SportActivity?) {
        if (sportActivity != null) {
            caloriesCalculator.setActivity(sportActivity)
        }
    }

    fun getCalories(sec: Long): Double {
        return caloriesCalculator.getCalories(sec)
    }

    override fun onUserUpdated(user: User) {
        caloriesCalculator.setUser(user)
    }

}