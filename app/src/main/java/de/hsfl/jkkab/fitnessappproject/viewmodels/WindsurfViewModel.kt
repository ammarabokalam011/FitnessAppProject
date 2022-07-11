package de.hsfl.jkkab.fitnessappproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import de.hsfl.jkkab.fitnessappproject.repositories.LocationRepository
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.repositories.StopwatchRepository

/**
 * ExerciseViewModel
 *
 * Vorläufiges ViewModel zum Projekt FitnessApp.
 *
 * @author Kevin Blaue
 * @version 1.2 (21.05.2022)
 */
class WindsurfViewModel : ViewModel() {
    private var locationRepository: LocationRepository? = null
    private var stopwatchRepository: StopwatchRepository? = null
    private var repository: Repository? = null
    fun setRepository(repository: Repository) {
        this.repository = repository
        stopwatchRepository = repository.stopwatchRepository
        locationRepository = repository.locationRepository
    }

    fun toggleStartStopwatch() {
        stopwatchRepository?.toggleStartStopwatch()
        if (stopwatchRepository?.isStopwatchRunning?.value == true) {
            locationRepository?.setStartLocation()
        } else {
            locationRepository?.resetStartLocation()
        }
    }

    fun togglePauseResumeStopwatch() {
        stopwatchRepository?.togglePauseResumeStopwatch()
    }

    val bearing: LiveData<Float>?
        get() = locationRepository?.bearing
    val averageBearing: LiveData<Double>?
        get() = locationRepository?.bearingAverage
    val distanceToStart: LiveData<Float>?
        get() = locationRepository?.distanceToStart
    val speed: LiveData<Float>?
        get() = locationRepository?.speed
    val time: LiveData<String>?
        get() = stopwatchRepository?.time
    val elapsedTime: LiveData<String>?
        get() = stopwatchRepository?.elapsedTime
    val isStopwatchStarted: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchStarted
    val isStopwatchRunning: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchRunning
}