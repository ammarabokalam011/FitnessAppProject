package de.hsfl.jkkab.fitnessappproject.repositories

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import de.hsfl.tjwa.blheartrateconnection.HeartSensorController
import java.lang.Exception

class HeartRateRepository(activity: Activity?) {
    private val heartSensorController: HeartSensorController
    private var countRates = 0
    private var totalRates: Long = 0
    private val heartRateAverage: MutableLiveData<Double> = MutableLiveData<Double>(0.0)
    fun startSimulation() {
        heartSensorController.startSimulation(1000)
    }

    fun connectHeartRateSensor() {
        heartSensorController.startBluetooth(false)
    }

    fun getHeartRateAverage(): LiveData<Double> {
        return heartRateAverage
    }

    val heartRate: LiveData<Int>
        get() = MutableLiveData(heartSensorController.getHeartRate().value)

    val isConnected: LiveData<Boolean>
        get() = heartSensorController.isConnected

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val permissionsNullable=permissions.copyOf(permissions.size)
        heartSensorController.onRequestPermissionsResult(requestCode, permissionsNullable, grantResults)
    }

    init {
        heartSensorController = HeartSensorController(activity!!)
        heartSensorController.getHeartRate().observeForever { integer ->
            countRates++
            totalRates += integer!!.toLong()
            heartRateAverage.setValue((totalRates / countRates).toDouble())
        }
    }
}