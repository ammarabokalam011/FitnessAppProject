package de.hsfl.jkkab.fitnessappproject.models

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.MainActivity
import java.util.*

/**
 * @author Kevin Blaue
 * @version 1.0 (21.05.2022)
 */
class LocationController : LocationListener {
    private val TAG = "LocationController"
    private var locationManager: LocationManager? = null
    private var context: Context? = null
    var startPoint: Location? = null
    private val location: MutableLiveData<Location>
    private val distance: MutableLiveData<Float>
    private val distanceToStart: MutableLiveData<Float>
    private val speed: MutableLiveData<Float>
    private val speedAverage: MutableLiveData<Double>
    private val bearing: MutableLiveData<Float>
    private val bearingAverage: MutableLiveData<Double>
    private val errorString: MutableLiveData<String>
    private var totalSpeed: Long = 0
    private var countSpeed = 0
    private var totalBearing: Long = 0
    private var countBearing = 0

    //Simulation
    private var simulationTimer: Timer? = null

    /**
     * Gibt zurück, ob die Positionsdaten real oder simuliert sind
     * @return true wenn Positionen simuliert werden
     */
    var isSimulating = false
        private set
    private var simIndex = 0
    private val simLocations: MutableList<Location>
    private var simLocationTime = 0f
    private var updatePeriod = 0

    /**
     * Startet die Aktualisierung der Position per Simulation
     */
    fun startSimulation(updatePeriod: Int) {
        stopAll()
        isSimulating = true
        this.updatePeriod = updatePeriod
        simulationTimer = Timer()
        simulationTimer!!.scheduleAtFixedRate(simulationTimerTask, 0, updatePeriod.toLong())
    }

    /**
     * Startet die Aktualisierung Position per Realposition
     * @param context Kontext
     */
    fun startGPS(context: Context?) {
        stopAll()
        this.context = context
        distance.setValue(0f)
        isSimulating = false
    }

    /**
     * stoppt GPS-Updates und das Generieren von simulierten Positionen
     */
    fun stopAll() {
        locationManager?.removeUpdates(this)
        if (simulationTimer != null) {
            simulationTimer!!.cancel()
            simulationTimer = null
        }
    }

    /**
     * Registriert den LocationListener oder fängt an
     * per Timer Positionen zu simulieren
     */
    fun addLocationListener() {
        locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, R.string.missing_location_permission, Toast.LENGTH_LONG).show()
            errorString.setValue(context!!.getString(R.string.missing_location_permission))
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1f, this)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MainActivity.REQUEST_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if (!isSimulating) {
                    addLocationListener()
                }
            } else {
                Log.d(TAG, "location permission denied")
            }
        }
    }

    private val simulationTimerTask: TimerTask = object : TimerTask() {
        override fun run() {
            simulate(1f / 10f * updatePeriod * simLocationTime)
            simLocationTime += updatePeriod.toFloat()
        }
    }

    /**
     * nächste Position simulieren
     * @param percent Fortschritt zwischen zwei Wegpunkten
     */
    private fun simulate(percent: Float) {
        var percent = percent
        if (percent > 1) {
            percent = 0f
            simLocationTime = 0f
            simIndex++
        }
        if (simIndex < simLocations.size) {
            val p1 = simLocations[simIndex]
            val p2: Location = when (simIndex) {
                0 -> {
                    simLocations[simLocations.size - 1]
                }
                simLocations.size - 1 -> {
                    simLocations[0]
                }
                else -> {
                    simLocations[simIndex + 1]
                }
            }
            val lng = p1.longitude - (p1.longitude - p2.longitude) * percent
            val lat = p1.latitude - (p1.latitude - p2.latitude) * percent
            val simulatedLocation: Location = Location(LocationManager.GPS_PROVIDER)
            simulatedLocation.latitude = lat
            simulatedLocation.longitude = lng
            location.postValue(simulatedLocation)
        } else {
            simIndex = 0
        }
    }

    /**
     * Setzt den momentanen Punkt als Startpunkt
     */
    fun setStartLocation() {
        startPoint = gPSPosition.value
    }

    /**
     * Reset Startpunkt
     */
    fun resetStartLocation() {
        startPoint = null
    }

    /**
     * Gibt den letzten Fehler zurück
     * @return Fehlermeldung | null wenn kein Fehler auftritt
     */
    fun getErrorString(): LiveData<String> {
        return errorString
    }

    /**
     * Gibt die aktuelle oder simulierte Position zurück
     * @return Positionsdaten
     */
    val gPSPosition: LiveData<Location>
        get() = location

    /**
     * Gibt die zurückgelegte Distanz zurück
     * @return Distanz
     */
    fun getDistance(): LiveData<Float> {
        return distance
    }

    /**
     * Gibt die zurückgelegte Distanz vom Start zurück
     * @return Distanz
     */
    fun getDistanceToStart(): LiveData<Float> {
        return distanceToStart
    }

    /**
     * Gibt die aktuelle Geschwindigkeit zurück
     * @return Geschwindigkeit in km/h
     */
    fun getSpeed(): LiveData<Float> {
        return speed
    }

    /**
     * Gibt die Durchschnittsgeschwindigkeit zurück
     * @return Geschwindigkeit in km/h
     */
    fun getSpeedAverage(): LiveData<Double> {
        return speedAverage
    }

    /**
     * Gibt den aktuellen Kurs zurück
     * @return Kurs in Grad
     */
    fun getBearing(): LiveData<Float> {
        return bearing
    }

    /**
     * Gibt den durchschnittlichen Kurs zurück
     * @return Kurs in Grad
     */
    fun getBearingAverage(): LiveData<Double> {
        return bearingAverage
    }

    /**
     * Gibt den durchschnittlichen Kurs zurück
     * @return Kurs in Grad
     */
    fun getgPSPosition(): LiveData<Location> {
        return gPSPosition
    }

    /**
     * Liste der in der Simulation abzulaufenden Positionen
     * @return Liste aus Location
     */
    val simPositions: List<Location>
        get() = simLocations

    override fun onLocationChanged(location: Location) {
        val prevLocation: Location? = this.location.value
        this.location.setValue(location)
        speed.setValue(location.speed)
        totalSpeed += location.speed.toLong()
        countSpeed++
        speedAverage.setValue((totalSpeed / countSpeed).toDouble())
        bearing.setValue(location.bearing)
        totalBearing += location.bearing.toLong()
        countBearing++
        bearingAverage.setValue((totalBearing / countBearing).toDouble())
        if (prevLocation != null) {
            distance.setValue(distance.value?.plus(location.distanceTo(prevLocation)))
        }
        if (startPoint != null) {
            distanceToStart.setValue(location.distanceTo(startPoint))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d(TAG, "$provider Status: $status")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG, "Provider enabled: $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(TAG, "Provider disabled: $provider")
    }

    init {
        simLocations = ArrayList()
        location = MutableLiveData<Location>()
        distance = MutableLiveData<Float>(0f)
        distanceToStart = MutableLiveData<Float>(0f)
        speed = MutableLiveData<Float>(0f)
        speedAverage = MutableLiveData<Double>(0.0)
        bearing = MutableLiveData<Float>(0f)
        bearingAverage = MutableLiveData<Double>(0.0)
        errorString = MutableLiveData<String>()

        //HS
        val p1: Location = Location(LocationManager.GPS_PROVIDER)
        p1.latitude = 54.7741329501
        p1.longitude = 9.44935798645

        //Hafen
        val p2: Location = Location(LocationManager.GPS_PROVIDER)
        p2.latitude = 54.7875966789
        p2.longitude = 9.43502426147
        simLocations.add(p2)
        simLocations.add(p1)
    }
}