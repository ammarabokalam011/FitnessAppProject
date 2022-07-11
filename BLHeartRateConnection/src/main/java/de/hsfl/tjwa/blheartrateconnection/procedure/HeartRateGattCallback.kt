package de.hsfl.tjwa.blheartrateconnection.procedure

import android.bluetooth.*
import android.util.Log
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
import java.util.*

/**
 * Created by Tjorben Wade on 07.01.2020.
 * Project: BluetoothHeartSensor
 *
 * Orientiert an https://medium.com/@avigezerit/bluetooth-low-energy-on-android-22bc7310387a
 */
class HeartRateGattCallback(private val mController: Controller) : BluetoothGattCallback() {
    private var mGatt: BluetoothGatt? = null
    private val HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D) //HEART RATE SERVICE
    private val HEART_RATE_MEASUREMENT_CHAR_UUID =
        convertFromInteger(0x2A37) //Eigenschaft zum Senden der Herzfrequenzmessung (Characteristic) --> https://www.bluetooth.com/specifications/gatt/characteristics/
    private val HEART_RATE_CONTROL_POINT_CHAR_UUID =
        convertFromInteger(0x2A39) //Eigenschaft zum Schreiben von Kontrollpunkten auf dem Gatt-Server, um das Verhalten zu steuern (Characteristic)

    /**
     * Kovertiert eine Integer in eine UUID
     * Übernommen von https://medium.com/@avigezerit/bluetooth-low-energy-on-android-22bc7310387a
     * @param i Integer zum konvertieren
     * @return UUID
     */
    fun convertFromInteger(i: Int): UUID {
        val MSB = 0x0000000000001000L
        val LSB = -0x7fffff7fa064cb05L
        return UUID(MSB or (i.toLong() shl 32), LSB)
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        mGatt = gatt
        Log.d(TAG, "CallbackState: $newState")
        if (newState == BluetoothAdapter.STATE_CONNECTED) {
            //Wenn eine Verbindung hergestellt wurde, sollen die Services des Gatt-Services herausgefunden werden
            Log.d(TAG, "Gatt Connected")
            gatt.discoverServices()
            mController.obtainMessage(SmMessage.GATT_CONNECTED.ordinal, 0, 0, null).sendToTarget()
        } else if (newState == BluetoothAdapter.STATE_DISCONNECTED) {
            mGatt!!.disconnect()
            mGatt!!.close()
            Log.d(TAG, "Gatt Disconnected")
            mController.obtainMessage(SmMessage.GATT_DISCONNECTED.ordinal, 0, 0, null)
                .sendToTarget()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        //Aktivieren der Benachrichtigungen für die Herzfrequenzmessung
        val characteristic = gatt.getService(HEART_RATE_SERVICE_UUID)
            .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)
        gatt.setCharacteristicNotification(characteristic, true)

        //Auf den Deskriptor schreiben um Benachrichtigungen zu aktivieren
        val CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902)
        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ) {
        //Auf die Charakteristik für den Kontrollpunkt schreiben, dass der Sensor mit dem Streamen starten soll
        val characteristic = gatt.getService(HEART_RATE_SERVICE_UUID)
            .getCharacteristic(HEART_RATE_CONTROL_POINT_CHAR_UUID)
        if (characteristic != null) {
            characteristic.value = byteArrayOf(1, 1) //Wird als Streaming Befehl verstanden
            gatt.writeCharacteristic(characteristic) //Schreiben
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        val flag = characteristic.properties
        val format: Int
        //Format UINT16 (max 65535) oder UINT8 (max 255)
        Log.d(TAG, String.format("Flag: %d", flag))
        if (flag and 0x01 != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16
            Log.d(TAG, "Herzratenformat UINT16.")
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8
            Log.d(TAG, "Herzratenformat UINT8.")
        }
        //Laden der Herzrate von Characteristic unter Angabe des Formats
        val heartRate = characteristic.getIntValue(format, 1)
        Log.d(TAG, String.format("Empfangen der Herzrate: %d", heartRate))
        mController.obtainMessage(SmMessage.HEART_RATE_RECEIVED.ordinal, 0, 0, heartRate)
            .sendToTarget()
    }

    fun close() {
        if (mGatt != null) {
            mGatt!!.close()
        }
    }

    companion object {
        private const val TAG = "bthsGattCallback"
    }
}