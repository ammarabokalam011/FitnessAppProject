package de.hsfl.tjwa.blheartrateconnection.procedure

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Message
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceListAdapter.DeviceViewHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceListAdapter
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceScanActivity
import de.hsfl.tjwa.blheartrateconnection.procedure.StateMachine
import de.hsfl.tjwa.blheartrateconnection.procedure.BluetoothController
import de.hsfl.tjwa.blheartrateconnection.procedure.OnBluetoothListener
import de.hsfl.tjwa.blheartrateconnection.procedure.HeartRateGattCallback
import de.hsfl.tjwa.blheartrateconnection.procedure.Controller.SmMessage
import de.hsfl.tjwa.blheartrateconnection.HeartSensorController

/**
 * Controller
 * Project: BluetoothHeartSensor
 *
 * Steuerklasse für das Projekt BluetoothHeartSensor.
 *
 *
 * @author Tjorben Wade
 * @version 1.3 (06.11.2020)
 */
class Controller : StateMachine(), BluetoothController {
    //Requests
    private val REQUEST_SCAN_DEVICE = 42
    private val REQUEST_ENABLE_BT = 242
    private val REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 142

    //SharedPreferences
    private val PREFS_KEY = "LE_BTHS_SHARED_PREFERENCES"
    private val PREFS_DEVICE_KEY = "LE_LATEST_DEVICE_ADDRESS"
    private var activity: Activity? = null
    private var mUiListener: OnBluetoothListener? = null
    private var mDevice: BluetoothDevice? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var gattCallback: HeartRateGattCallback? = null
    private var bluetoothDialogLauncher: ActivityResultLauncher<Intent>? = null
    private var deviceListLauncher: ActivityResultLauncher<Intent>? = null

    enum class SmMessage {
        CONNECT_LOADED_DEVICE, UI_CONNECT_NEW_DEVICE, UI_STOP,  // from UI
        CO_INIT, BLUETOOTH_INIT,  // to Controller
        BLUETOOTH_ENABLED, PERMISSION_GRANTED, ENABLE_BLUETOOTH_TIMEOUT, GRANT_PERMISSION_TIMEOUT, GATT_CONNECTED, GATT_DISCONNECTED, HEART_RATE_RECEIVED //from Callback
    }

    private enum class State {
        START, WAIT_FOR_BLUETOOTH, WAIT_FOR_PERMISSION, IDLE, CONNECTED, NO_BLUETOOTH
    }

    private var state = State.START // the state variable
    private fun setState(state: State) {
        Log.d(TAG, "setState(): " + state.name)
        this.state = state
        mUiListener!!.onStateChanged(state.toString())
    }

    override fun init(a: Activity?) {
        Log.d(TAG, "init()")
        activity = a
        sendSmMessage(SmMessage.CO_INIT.ordinal, 0, 0, null)
        deviceListLauncher = (activity as AppCompatActivity?)!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //Ergebnis von Gerätesuche-Activity
            if (result.data != null && result.data!!.hasExtra(LeDeviceScanActivity.Companion.SELECTED_DEVICE)) {
                val device = result.data!!
                    .extras!![LeDeviceScanActivity.Companion.SELECTED_DEVICE] as BluetoothDevice?
                if (device != null) {
                    Log.d(TAG, "Selected Device: " + device.name)

                    //Gerät laden und verbinden
                    mDevice = device
                    sendSmMessage(SmMessage.CONNECT_LOADED_DEVICE.ordinal, 0, 0, null)
                } else {
                    mUiListener!!.onError(
                        BluetoothController.Companion.ERROR_NO_SELECTED_DEVICE,
                        "no selected device"
                    )
                }
            } else {
                mUiListener!!.onError(
                    BluetoothController.Companion.ERROR_NO_SELECTED_DEVICE,
                    "no selected device"
                )
            }
        }
        bluetoothDialogLauncher =
            (activity as AppCompatActivity?)!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result?.resultCode == Activity.RESULT_OK) {
                    sendSmMessage(SmMessage.BLUETOOTH_ENABLED.ordinal, 0, 0, null)
                }
            }
    }

    override fun setListener(onBluetoothListener: OnBluetoothListener?) {
        Log.d(TAG, "setListener()")
        mUiListener = onBluetoothListener
    }

    override fun start(useLatestDevice: Boolean) {
        if (useLatestDevice) {
            val pref = activity!!.getSharedPreferences(PREFS_KEY, 0)
            //Prüfen ob ein Gerät gespeichert wurde
            if (pref.contains(PREFS_DEVICE_KEY)) {
                mDevice = mBluetoothAdapter!!.getRemoteDevice(pref.getString(PREFS_DEVICE_KEY, ""))
                sendSmMessage(SmMessage.CONNECT_LOADED_DEVICE.ordinal, 0, 0, null)
            } else {
                mUiListener!!.onError(
                    BluetoothController.Companion.ERROR_NO_STORED_DEVICE,
                    "no stored bluetooth device"
                )
            }
        } else {
            sendSmMessage(SmMessage.UI_CONNECT_NEW_DEVICE.ordinal, 0, 0, null)
        }
    }

    override fun start(device: BluetoothDevice?) {
        mDevice = device
        sendSmMessage(SmMessage.CONNECT_LOADED_DEVICE.ordinal, 0, 0, null)
    }

    override fun stop() {
        sendSmMessage(SmMessage.UI_STOP.ordinal, 0, 0, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                sendSmMessage(SmMessage.PERMISSION_GRANTED.ordinal, 0, 0, null)
            } else {
                Log.d(TAG, "location permission denied")
            }
        }
    }

    //Speichern des momentanen Gerätes
    private fun storeLoadedDevice() {
        if (mDevice != null) {
            val pref = activity!!.getSharedPreferences(PREFS_KEY, 0)
            val editor = pref.edit()
            editor.putString(PREFS_DEVICE_KEY, mDevice!!.address)
            editor.apply()
        }
    }

    /**
     * the statemachine
     *
     * call it only via sendSmMessage()
     *
     * @param message Message
     */
    public override fun theBrain(message: Message) {
        val inputSmMessage = messageIndex[message.what]
        Log.i(TAG, "SM: Message: " + inputSmMessage.name)
        when (state) {
            State.START -> when (inputSmMessage) {
                SmMessage.CO_INIT -> {
                    Log.d(TAG, "Init Bluetooth")
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (mBluetoothAdapter == null) {
                        Log.d(TAG, "Error: Device does not support Bluetooth!!!")
                        setState(State.NO_BLUETOOTH)
                         // case SmMessage CO_INIT
                    }
                    if (!mBluetoothAdapter!!.isEnabled) {
                        Log.d(TAG, "Bluetooth is not enabled!")

                        //Zeige eine Meldung zum Aktivieren von Bluetooth
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        //activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        bluetoothDialogLauncher!!.launch(enableBtIntent)
                        setTimer(SmMessage.ENABLE_BLUETOOTH_TIMEOUT.ordinal, 10000)
                        setState(State.WAIT_FOR_BLUETOOTH)
                        // case SmMessage BLUETOOTH_INIT
                    }

                    //Bluetooth ist aktiviert
                    sendSmMessage(SmMessage.BLUETOOTH_ENABLED.ordinal, 0, 0, null)
                    setState(State.WAIT_FOR_BLUETOOTH)
                }
                SmMessage.BLUETOOTH_INIT -> {
                    if (!mBluetoothAdapter!!.isEnabled) {
                        Log.d(TAG, "Bluetooth is not enabled!")
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        bluetoothDialogLauncher!!.launch(enableBtIntent)
                        setTimer(SmMessage.ENABLE_BLUETOOTH_TIMEOUT.ordinal, 10000)
                        setState(State.WAIT_FOR_BLUETOOTH)
                    }
                    sendSmMessage(SmMessage.BLUETOOTH_ENABLED.ordinal, 0, 0, null)
                    setState(State.WAIT_FOR_BLUETOOTH)
                }
                else -> Log.v(TAG, "CO-SM: not a valid input in this state: $inputSmMessage")
            }
            State.WAIT_FOR_BLUETOOTH -> when (inputSmMessage) {
                SmMessage.BLUETOOTH_ENABLED -> {
                    //Timer für Timeout stoppen
                    stopTimer(SmMessage.ENABLE_BLUETOOTH_TIMEOUT.ordinal)
                    //Bluetooth ist aktiviert. Nun soll die Berechtigung überprüft werden.
                    if (ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_PERMISSION_ACCESS_FINE_LOCATION
                        )
                        Log.d(TAG, "No location permission to scan for ble devices")
                        setTimer(SmMessage.GRANT_PERMISSION_TIMEOUT.ordinal, 10000)
                        setState(State.WAIT_FOR_PERMISSION)

                    }
                    //Berechtigung bereits erteilt
                    sendSmMessage(SmMessage.PERMISSION_GRANTED.ordinal, 0, 0, null)
                    setState(State.WAIT_FOR_PERMISSION)
                }
                SmMessage.ENABLE_BLUETOOTH_TIMEOUT -> {
                    mUiListener!!.onError(
                        BluetoothController.Companion.ERROR_BLUETOOTH_TIMEOUT,
                        "bluetooth timeout"
                    )
                    sendSmMessage(SmMessage.BLUETOOTH_INIT.ordinal, 0, 0, null)
                    setState(State.START)
                }
                else -> Log.v(
                    TAG,
                    "WAIT_FOR_BLUETOOTH-SM: not a valid input in this state: $inputSmMessage"
                )
            }
            State.WAIT_FOR_PERMISSION -> when (inputSmMessage) {
                SmMessage.PERMISSION_GRANTED -> {
                    //Timer für Timeout stoppen
                    stopTimer(SmMessage.GRANT_PERMISSION_TIMEOUT.ordinal)
                    setState(State.IDLE)
                }
                SmMessage.GRANT_PERMISSION_TIMEOUT -> {
                    mUiListener!!.onError(
                        BluetoothController.Companion.ERROR_LOCATION_TIMEOUT,
                        "location timeout"
                    )
                    sendSmMessage(SmMessage.BLUETOOTH_ENABLED.ordinal, 0, 0, null)
                    setState(State.WAIT_FOR_BLUETOOTH)
                }
                else -> Log.v(
                    TAG,
                    "WAIT_FOR_PERMISSION-SM: not a valid input in this state: $inputSmMessage"
                )
            }
            State.IDLE -> when (inputSmMessage) {
                SmMessage.UI_CONNECT_NEW_DEVICE -> {
                    val intent = Intent(activity, LeDeviceScanActivity::class.java)
                    //activity.startActivityForResult(intent, REQUEST_SCAN_DEVICE, a);
                    deviceListLauncher!!.launch(intent)
                    Log.v(TAG, "ui connect new device")
                }
                SmMessage.CONNECT_LOADED_DEVICE -> {
                    Log.v(TAG, "connecting to device")
                    gattCallback = HeartRateGattCallback(this)
                    //Mit dem Gerät verbinden
                    mDevice!!.connectGatt(activity, true, gattCallback)
                    setState(State.CONNECTED)
                }
                else -> Log.v(TAG, "IDLE-SM: not a valid input in this state: $inputSmMessage")
            }
            State.CONNECTED -> when (inputSmMessage) {
                SmMessage.GATT_CONNECTED -> {
                    mUiListener!!.onConnectionChanged(true, mDevice)
                    storeLoadedDevice() //Speichern des Gerätes
                }
                SmMessage.HEART_RATE_RECEIVED -> {
                    val heartRate = message.obj as Int
                    mUiListener!!.onHeartRateUpdated(heartRate)
                }
                SmMessage.UI_STOP -> {
                    mUiListener!!.onConnectionChanged(false, mDevice)
                    if (gattCallback != null) {
                        gattCallback!!.close()
                    }
                    setState(State.IDLE)
                }
                SmMessage.GATT_DISCONNECTED -> {
                    Log.v(TAG, "gatt disconnected")
                    mUiListener!!.onConnectionChanged(false, mDevice)
                    setState(State.IDLE)
                }
                else -> Log.v(TAG, "CONNECTED-SM: not a valid input in this state: $inputSmMessage")
            }
            State.NO_BLUETOOTH -> mUiListener!!.onError(
                BluetoothController.Companion.ERROR_NO_BLUETOOTH,
                "no bluetooth"
            )
        }
    }

    companion object {
        private const val TAG = "bthsController"
        var messageIndex = SmMessage.values()
        val bluetoothController: BluetoothController
            get() {
                Log.d(TAG, "getBluetoothController()")
                return Controller()
            }
    }

    init {
        Log.d(TAG, "Controller()")
    }
}