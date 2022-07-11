package de.hsfl.tjwa.blheartrateconnection.scan

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
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
import de.hsfl.tjwa.blheartrateconnection.R
import java.util.ArrayList

/**
 * Created by Gorb98 on 20.11.2019.
 * Project: BluetoothHerzsensor
 */
class LeDeviceListAdapter(private val context: Context) : BaseAdapter() {
    private val mLeDevices: ArrayList<BluetoothDevice>
    fun addDevice(device: BluetoothDevice) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice {
        return mLeDevices[position]
    }

    fun clear() {
        mLeDevices.clear()
    }

    override fun getCount(): Int {
        return mLeDevices.size
    }

    override fun getItem(i: Int): Any {
        return mLeDevices[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        val deviceView: DeviceViewHolder
        if (view == null) {
            //Layout listitem_device.xml übernehmen
            view = LayoutInflater.from(context).inflate(R.layout.listitem_device, viewGroup, false)

            //Textfelder zuordnen
            deviceView = DeviceViewHolder(view)
            view.tag = deviceView
        } else {
            //Vorhandene LayoutZuordnung laden
            deviceView = view.tag as DeviceViewHolder
        }

        //Gerätename und Adresse auf Textfeldern zeigen
        val device = mLeDevices[i]
        val deviceName = device.name
        if (deviceName != null && deviceName.length > 0) {
            //Ein Gerätename ist vorhanden
            deviceView.deviceName.text = deviceName
        } else {
            //Kein Gerätename bekannt
            deviceView.deviceName.text = context.getString(R.string.unknown_device)
        }
        deviceView.deviceAddress.text = device.address
        return view
    }

    inner class DeviceViewHolder(itemView: View) {
        var deviceAddress: TextView
        var deviceName: TextView

        init {
            deviceAddress = itemView.findViewById(R.id.device_address)
            deviceName = itemView.findViewById(R.id.device_name)
        }
    }

    init {
        mLeDevices = ArrayList()
    }
}