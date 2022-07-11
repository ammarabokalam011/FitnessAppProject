package de.hsfl.jkkab.fitnessappproject.repositories

import androidx.lifecycle.LiveData
import de.hsfl.jkkab.fitnessappproject.models.StopwatchController

class StopwatchRepository {
    private val stopwatchController: StopwatchController = StopwatchController()
    val time: LiveData<String>
        get() = stopwatchController.time
    val elapsedTime: LiveData<String>
        get() = stopwatchController.getElapsedString()
    val elapsedSeconds: LiveData<Long>
        get() = stopwatchController.getElapsedSeconds()

    fun startStopwatch() {
        stopwatchController.start()
    }

    fun stopStopwatch() {
        stopwatchController.stop()
    }

    fun pauseStopwatch() {
        stopwatchController.pause()
    }

    fun toggleStartStopwatch() {
        if (isStopwatchStarted.getValue() == true) {
            stopStopwatch()
        } else {
            startStopwatch()
        }
    }

    fun togglePauseResumeStopwatch() {
        if (isStopwatchRunning.getValue() == true) {
            pauseStopwatch()
        } else {
            startStopwatch()
        }
    }

    val isStopwatchStarted: LiveData<Boolean>
        get() = stopwatchController.isStarted()
    val isStopwatchRunning: LiveData<Boolean>
        get() = stopwatchController.isRunning()

}