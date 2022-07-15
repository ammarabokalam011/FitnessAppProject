package de.hsfl.jkkab.fitnessappproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hsfl.jkkab.fitnessappproject.repositories.CaloriesRepository
import de.hsfl.jkkab.fitnessappproject.repositories.LocationRepository
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.repositories.StopwatchRepository

class JoggingViewModel : ViewModel() {

    private var locationRepository: LocationRepository? = null
    private var stopwatchRepository: StopwatchRepository? = null
    private var repository: Repository? = null
    private var calories: MutableLiveData<Double>? = null

    fun setRepository(repository: Repository) {
        this.repository = repository
        calories = MutableLiveData<Double>(0.0)
        stopwatchRepository = repository.stopwatchRepository
        locationRepository = repository.locationRepository
        val caloriesRepository: CaloriesRepository? = repository.caloriesRepository
        stopwatchRepository?.elapsedSeconds
            ?.observeForever { sec ->
                if (caloriesRepository != null) {
                    calories!!.setValue(caloriesRepository.getCalories(sec))
                }
            }
    }

    fun toggleStartStopwatch() {
        stopwatchRepository?.toggleStartStopwatch()
    }

    fun togglePauseResumeStopwatch() {
        stopwatchRepository?.togglePauseResumeStopwatch()
    }

    val isConnected: LiveData<Boolean>?
        get() = repository!!.heartRateRepository?.isConnected
    val heartRate: LiveData<Int>?
        get() = repository!!.heartRateRepository?.heartRate
    val averageHeartRate: LiveData<Double>?
        get() = repository!!.heartRateRepository?.getHeartRateAverage()
    val distance: LiveData<Float>?
        get() = locationRepository?.distance
    val speed: LiveData<Float>?
        get() = locationRepository?.speed
    val averageSpeed: LiveData<Double>?
        get() = locationRepository?.speedAverage

    fun getCalories(): LiveData<Double>? {
        return calories
    }

    val time: LiveData<String>?
        get() = stopwatchRepository?.time
    val elapsedTime: LiveData<String>?
        get() = stopwatchRepository?.elapsedTime
    val isStopwatchStarted: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchStarted
    val isStopwatchRunning: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchRunning
}
