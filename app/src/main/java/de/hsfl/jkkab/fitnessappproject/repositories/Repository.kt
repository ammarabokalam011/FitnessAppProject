package de.hsfl.jkkab.fitnessappproject.repositories

import android.app.Activity

/**
 * Repository
 *
 * Repository
 *
 * @author Kevin Blaue
 * @version 1.0 (21.05.2022)
 */
class Repository(activity: Activity) {
    var userRepository: UserRepository? = null
        private set
    var heartRateRepository: HeartRateRepository? = null
        private set
    var locationRepository: LocationRepository? = null
        private set
    var caloriesRepository: CaloriesRepository? = null
        private set
    var stopwatchRepository: StopwatchRepository? = null
        private set

    fun init(activity: Activity) {
        userRepository = UserRepository(activity.application)
        heartRateRepository = HeartRateRepository(activity)
        locationRepository = LocationRepository(activity)
        caloriesRepository = CaloriesRepository()
        stopwatchRepository = StopwatchRepository()

        //Listener
        userRepository!!.setOnUserUpdated(caloriesRepository)
        heartRateRepository!!.startSimulation()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        heartRateRepository!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationRepository!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    init {
        init(activity)
    }
}