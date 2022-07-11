package de.hsfl.jkkab.fitnessappproject.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hsfl.jkkab.fitnessappproject.repositories.LocationRepository
import de.hsfl.jkkab.fitnessappproject.repositories.Repository
import de.hsfl.jkkab.fitnessappproject.repositories.StopwatchRepository

class TrackViewModel : ViewModel() {
    private var locationRepository: LocationRepository? = null
    private var stopwatchRepository: StopwatchRepository? = null
    private var syncCenter: MutableLiveData<Boolean>? = null
    fun setRepository(repository: Repository) {
        syncCenter = MutableLiveData<Boolean>(true)
        locationRepository = repository.locationRepository
        stopwatchRepository = repository.stopwatchRepository
    }

    val speed: LiveData<Float>?
        get() = locationRepository?.speed
    val location: LiveData<Location>?
        get() = locationRepository?.position

    fun syncCenter() {
        syncCenter?.setValue(true)
    }

    fun isSyncCenter(): MutableLiveData<Boolean>? {
        return syncCenter
    }

    val isStopwatchStarted: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchStarted
    val isStopwatchRunning: LiveData<Boolean>?
        get() = stopwatchRepository?.isStopwatchRunning

    fun toggleStartStopwatch() {
        stopwatchRepository?.toggleStartStopwatch()
    }

    fun togglePauseResumeStopwatch() {
        stopwatchRepository?.togglePauseResumeStopwatch()
    }
}