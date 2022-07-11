package de.hsfl.jkkab.fitnessappproject.models

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

class StopwatchController {
    private val handler: Handler = Handler(Looper.getMainLooper())
    val dfClock = SimpleDateFormat("HH:mm", Locale.GERMAN)
    val df = SimpleDateFormat("HH:mm:ss", Locale.GERMAN)
    private var startTime: Long = 0
    private var pauseTime: Long = 0
    private val running: MutableLiveData<Boolean>
    private val started: MutableLiveData<Boolean>
    private var elapsedSeconds: MutableLiveData<Long> = MutableLiveData()
    private var elapsedString: MutableLiveData<String> = MutableLiveData()
    private val timeString: MutableLiveData<String>
    fun start() {
        started.value = true
        running.value = true
        startTime = System.currentTimeMillis() - (pauseTime - startTime)
        handler.postDelayed(rUpdater, 0)
    }

    private val rUpdater: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            elapsedSeconds.value = millis / 1000
            elapsedString.postValue(df.format(millis))
            handler.postDelayed(this, 1000)
        }
    }

    fun pause() {
        running.value = false
        pauseTime = System.currentTimeMillis()
        handler.removeCallbacks(rUpdater)
    }

    fun stop() {
        pause()
        started.value = false
        startTime = 0
        pauseTime = 0
    }

    fun isStarted(): LiveData<Boolean> {
        return started
    }

    fun isRunning(): LiveData<Boolean> {
        return running
    }

    fun getElapsedString(): LiveData<String> {
        return elapsedString
    }

    fun getElapsedSeconds(): LiveData<Long> {
        return elapsedSeconds
    }

    val time: LiveData<String>
        get() = timeString

    init {
        df.timeZone = TimeZone.getTimeZone("GMT")
        running = MutableLiveData(false)
        started = MutableLiveData(false)
        elapsedSeconds = MutableLiveData()
        elapsedString = MutableLiveData()
        timeString = MutableLiveData()
        val timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                timeString.postValue(dfClock.format(Date()))
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 1000)
    }
}