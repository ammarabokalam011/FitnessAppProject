package de.hsfl.tjwa.blheartrateconnection.procedure

import android.app.Activity
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

interface BluetoothController {
    fun init(a: Activity?)
    fun setListener(onBluetoothListener: OnBluetoothListener?) // falls BT von einer Activity aus genutzt wird
    fun start(useLastDevice: Boolean)
    fun start(device: BluetoothDevice?)
    fun stop()

    //void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    )

    companion object {
        const val ERROR_NO_STORED_DEVICE = 42001
        const val ERROR_NO_BLUETOOTH = 42002
        const val ERROR_BLUETOOTH_TIMEOUT = 42003
        const val ERROR_LOCATION_TIMEOUT = 42004
        const val ERROR_NO_SELECTED_DEVICE = 42005
        const val NO_ERROR = 42200
    }
}