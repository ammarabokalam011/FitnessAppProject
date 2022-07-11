package de.hsfl.jkkab.fitnessappproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hsfl.jkkab.fitnessappproject.repositories.CaloriesRepository
import de.hsfl.jkkab.fitnessappproject.repositories.HeartRateRepository
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.repositories.StopwatchRepository

class CardioViewModel : ViewModel() {
    private var repository: Repository? = null
    private var stopwatchRepository: StopwatchRepository? = null
    private var heartRateRepository: HeartRateRepository? = null
    private var calories: MutableLiveData<Double>? = null
    fun setRepository(repository: Repository) {
        this.repository = repository
        stopwatchRepository = repository.stopwatchRepository
        heartRateRepository = repository.heartRateRepository
        calories = MutableLiveData<Double>(0.0)
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

    val heartRateAverage: LiveData<Double>?
        get() = heartRateRepository?.getHeartRateAverage()
    val heartRate: LiveData<Int>?
        get() = repository!!.heartRateRepository?.heartRate
    val time: LiveData<String>?
        get() = stopwatchRepository?.time
    val elapsedTime: LiveData<String>?
        get() = stopwatchRepository?.elapsedTime
    val elapsedSeconds: LiveData<Long>?
        get() = stopwatchRepository?.elapsedSeconds
    val isStopwatchStarted: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchStarted
    val isStopwatchRunning: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchRunning

    fun getCalories(): LiveData<Double>? {
        return calories
    }
}