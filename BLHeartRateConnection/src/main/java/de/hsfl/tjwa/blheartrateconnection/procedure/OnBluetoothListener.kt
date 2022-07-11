package de.hsfl.tjwa.blheartrateconnection.procedure

import android.bluetooth.BluetoothDevice
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceListAdapter.DeviceViewHolder
import androidx.appcompat.app.AppCompatActivity
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceListAdapter
import de.hsfl.tjwa.blheartrateconnection.scan.LeDeviceScanActivity
import de.hsfl.tjwa.blheartrateconnection.procedure.StateMachine
import de.hsfl.tjwa.blheartrateconnection.procedure.BluetoothController
import de.hsfl.tjwa.blheartrateconnection.procedure.OnBluetoothListener
import de.hsfl.tjwa.blheartrateconnection.procedure.HeartRateGattCallback
import de.hsfl.tjwa.blheartrateconnection.procedure.Controller.SmMessage
import de.hsfl.tjwa.blheartrateconnection.HeartSensorController

interface OnBluetoothListener {
    fun onHeartRateUpdated(heartRate: Int) //Neue Herzrate empfangen
    fun onStateChanged(strState: String) //Status der Steuerklasse hat sich geändert
    fun onConnectionChanged(
        connected: Boolean,
        device: BluetoothDevice?
    ) //Status der Verbindung zum Gatt hat sich geändert

    fun onError(code: Int, message: String?)
}