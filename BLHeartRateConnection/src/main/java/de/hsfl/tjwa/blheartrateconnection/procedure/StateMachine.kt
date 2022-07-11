package de.hsfl.tjwa.blheartrateconnection.procedure

import android.os.Handler
import android.os.Message
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

/**
 * Übernommen aus BtRfServerClient
 *
 * Statemachine Klasse
 *
 * wird im Projekt SmParkingMeter2 gepflegt.
 *
 * History:
 * 07.12.15 tas Erstellung
 *
 */
open class StateMachine : Handler() {
    override fun handleMessage(message: Message) {
        theBrain(message)
    } //##0b

    /**
     * virtual method, must be overwritten in subclass
     * @param message
     */
    open fun theBrain(message: Message) {}
    protected fun setTimer(messageType: Int, durationMs: Long) {
        val msg = Message()
        msg.what = messageType
        msg.arg1 = 0
        msg.arg2 = 0
        sendMessageDelayed(msg, durationMs) //##4a
    }

    protected fun stopTimer(messageType: Int) {
        removeMessages(messageType) //##4b
    }

    fun sendSmMessage(messageType: Int, arg1: Int, arg2: Int, obj: Any?) {
        // PrintData d = new PrintData(3);                                                   //##3a
        val msg = Message() //##0a
        msg.what = messageType
        msg.arg1 = arg1
        msg.arg2 = arg2
        if (obj != null) {
            msg.obj = obj
        }
        sendMessage(msg)
    }

    companion object {
        private const val TAG = "hsflStateMachine"
    }
}