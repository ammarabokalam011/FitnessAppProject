package de.hsfl.jkkab.fitnessappproject.repositories

import android.app.Activity
import android.location.Location
import androidx.lifecycle.LiveData
import de.hsfl.jkkab.fitnessappproject.models.LocationController

class LocationRepository(activity: Activity?) {
    private val locationController: LocationController = LocationController()
    fun setStartLocation() {
        locationController.setStartLocation()
    }

    fun resetStartLocation() {
        locationController.resetStartLocation()
    }

    val bearing: LiveData<Float>
        get() = locationController.getBearing()
    val bearingAverage: LiveData<Double>
        get() = locationController.getBearingAverage()
    val speed: LiveData<Float>
        get() = locationController.getSpeed()
    val speedAverage: LiveData<Double>
        get() = locationController.getSpeedAverage()
    val position: LiveData<Location>
        get() = locationController.getgPSPosition()
    val distance: LiveData<Float>
        get() = locationController.getDistance()
    val distanceToStart: LiveData<Float>
        get() = locationController.getDistanceToStart()
    val errorString: LiveData<String>
        get() = locationController.getErrorString()

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    init {
        locationController.startGPS(activity)
    }
}