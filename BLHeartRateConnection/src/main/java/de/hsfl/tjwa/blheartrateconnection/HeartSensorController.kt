package de.hsfl.tjwa.blheartrateconnection

import android.app.Activity
import android.bluetooth.BluetoothDevice
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceListAdapter.DeviceViewHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceListAdapter
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceScanActivity
import de.hsfl.tjwa.blheartrateconnection.procedure.Controller.SmMessage
import de.hsfl.tjwa.blheartrateconnection.HeartSensorController
import de.hsfl.tjwa.blheartrateconnection.procedure.*
import java.util.*

/**
 * HeartSensorController
 * Hauptklasse des BLHeartRateConnection Projektes
 *
 * Stellt die Verbindung zum Projekt BluetoothHeartRate her
 * und stellt Herzdaten bereit.
 *
 * @author Tjorben Wade
 * @version 1.4 (06.11.2020)
 */
class HeartSensorController(  //nicht geteilt
    private val activity: Activity
) : OnBluetoothListener {
    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothController: BluetoothController? = null
    private var useLatestDevice = false
    private var connect = false

    //geteilte Daten
    private val errorCode: MutableLiveData<Int>
    private val errorString: MutableLiveData<String?>
    private val heartRate: MutableLiveData<Int?>
    private val connectionState: MutableLiveData<Boolean>
    private val connectedDevice: MutableLiveData<BluetoothDevice?>

    /**
     * Gibt zurück, ob Herzdaten momentan simuliert werden
     * @return true wenn Daten simuliert werden
     */
    //Simulation
    var isSimulating = false
        private set
    private var simulationTimer: Timer? = null

    /**
     * Startet eine Simulation einer Verbindung mit einem Herzsensor
     */
    fun startSimulation(updatePeriod: Int) {
        stopAll()
        isSimulating = true
        errorCode.setValue(BluetoothController.Companion.NO_ERROR)
        errorString.value = null
        simulationTimer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                simulateHeartRate(55, 150)
            }
        }
        simulationTimer!!.scheduleAtFixedRate(timerTask, 0, updatePeriod.toLong())
        connectionState.setValue(true)
    }

    /**
     * Startet den Prozess zum Verbindungsaufbau mit einem Bluetooth Herzsensor
     * @param useLatestDevice Letztes verbundenes Gerät nutzen
     */
    fun startBluetooth(useLatestDevice: Boolean) {
        this.useLatestDevice = useLatestDevice
        this.startBluetooth(null)
    }

    /**
     * Startet den Prozess zum Verbindungsaufbau mit einem Bluetooth Herzsensor
     * @param bluetoothDevice Bluetoothgerät, mit dem eine Verbindung aufgebaut werden soll
     */
    fun startBluetooth(bluetoothDevice: BluetoothDevice?) {
        stopAll()
        connect = true
        isSimulating = false
        errorCode.setValue(BluetoothController.Companion.NO_ERROR)
        errorString.value = null
        this.bluetoothDevice = bluetoothDevice
        bluetoothController = Controller.Companion.bluetoothController
        bluetoothController!!.init(activity)
        bluetoothController!!.setListener(this)
    }

    /**
     * Simuliert Herzraten
     */
    private fun simulateHeartRate(min: Int, max: Int) {
        if (heartRate.value == null || heartRate.value!! <= 0) {
            heartRate.postValue(randRange(min, max))
        } else {
            val maxSub = Math.min(heartRate.value!! - 55, 7)
            val maxAdd = Math.min(max - heartRate.value!!, 7)
            heartRate.postValue(heartRate.value!! + randRange(-maxSub, maxAdd))
        }
    }

    /**
     * Gibt die per Bluetooth empfangene oder
     * simulierte Herzrate zurück.
     * @return Herzrate
     */
    fun getHeartRate(): LiveData<Int?> {
        return heartRate
    }

    /**
     * Gibt zurück, ob ein Herzsensor verbunden ist
     * @return Verbindungsstatus
     */
    val isConnected: LiveData<Boolean>
        get() = connectionState

    /**
     * Gibt das verbundene Gerät zurück
     * @return Bluetooth-Gerät (null -> kein Gerät verbunden oder Simulation)
     */
    fun getConnectedDevice(): LiveData<BluetoothDevice?> {
        return connectedDevice
    }

    /**
     * Gibt den letzten Fehler zurück
     * @return Fehlermeldung | null wenn kein Fehler auftritt
     */
    fun getErrorString(): LiveData<String?> {
        return errorString
    }

    /**
     * Gibt den letzten Fehlercode zurück
     * @return Fehlercode (Entspricht einem Code aus BluetoothController)
     */
    fun getErrorCode(): LiveData<Int> {
        return errorCode
    }

    override fun onHeartRateUpdated(heartRate: Int) {
        this.heartRate.value = heartRate
    }

    override fun onStateChanged(strState: String) {
        if (strState == "IDLE" && bluetoothController != null && connect) {
            connect = false
            if (bluetoothDevice == null) {
                bluetoothController!!.start(useLatestDevice)
            } else {
                bluetoothController!!.start(bluetoothDevice)
            }
        }
    }

    override fun onConnectionChanged(connected: Boolean, device: BluetoothDevice?) {
        connectionState.value = connected
        connectedDevice.value = device
    }

    override fun onError(code: Int, message: String?) {
        if (code == BluetoothController.Companion.ERROR_BLUETOOTH_TIMEOUT) {
            //Toast.makeText(activity, activity.getString(R.string.bluetooth_not_enabled), Toast.LENGTH_LONG).show();
            errorString.setValue(activity.getString(R.string.bluetooth_not_enabled))
        } else if (code == BluetoothController.Companion.ERROR_LOCATION_TIMEOUT) {
            //Toast.makeText(activity, activity.getString(R.string.missing_permission), Toast.LENGTH_LONG).show();
            errorString.setValue(activity.getString(R.string.missing_permission))
        } else if (code == BluetoothController.Companion.ERROR_NO_SELECTED_DEVICE) {
            errorString.setValue(activity.getString(R.string.no_selected_device))
        } else {
            errorString.setValue(message)
            //Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
        errorCode.setValue(code)
    }

    /**
     * stoppt Bluetooth-Updates und das Generieren von simulierten Herzraten
     */
    fun stopAll() {
        connectionState.value = false
        if (bluetoothController != null) {
            bluetoothController!!.stop()
        }
        if (simulationTimer != null) {
            simulationTimer!!.cancel()
            simulationTimer = null
        }
    }

    /**
     * Verarbeitet zugeteilte Berechtigungen
     * TODO: in späteren Versionen entfernen
     * @param requestCode von onRequestPermissionsResult() der registrierten Activity
     * @param permissions von onRequestPermissionsResult() der registrierten Activity
     * @param grantResults von onRequestPermissionsResult() der registrierten Activity
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (bluetoothController != null) {
            bluetoothController!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private fun randRange(min: Int, max: Int): Int {
            return (Math.random() * (max - min) + min).toInt()
        }
    }

    init {
        errorCode = MutableLiveData()
        errorCode.setValue(BluetoothController.Companion.NO_ERROR)
        errorString = MutableLiveData()
        heartRate = MutableLiveData()
        connectionState = MutableLiveData()
        connectionState.value = false
        connectedDevice = MutableLiveData()
    }
}