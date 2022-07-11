package de.hsfl.tjwa.blheartrateconnection.scan

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
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

/**
 * DeviceScanActivity
 *
 * Die Activity scannt nach Bluetooth LE-Geräten. Das Scannen startet, wenn die
 * Activity geladen wird und stoppt nach 10 Sekunden. Durch den bToogleSearch-Button
 * kann das Scannen erneut gestartet werden.
 *
 * Wenn ein Bluetooth-LE-Gerät in der Liste ausgewählt wurde, liefert die Activity ein
 * Ergebnis zurück.
 *
 * Zum Scannen nach LE-Geräten wird seit API 23 die Permission ACCESS_FINE_LOCATION benötigt, da LE-Beacons
 * häufig mit dem Standort verknüpft werden.
 * Zum Nachlesen:
 * - https://developer.android.com/guide/topics/connectivity/bluetooth-le#permissions
 * - https://stackoverflow.com/questions/41716452/why-location-permission-are-required-for-ble-scan-in-android-marshmallow-onwards
 *
 * @author Tjorben Wade
 * @version 1.0 (15.12.2019)
 */
class LeDeviceScanActivity : AppCompatActivity() {
    //Button
    private var bToggleSearch: Button? = null
    private var leScanner: BluetoothLeScanner? = null
    private var isScanning = false
    private var handler: Handler? = null
    private var leDeviceListAdapter: LeDeviceListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        setContentView(R.layout.activity_device_scan)

        //Suchenbutton
        bToggleSearch = findViewById(R.id.bToggleSearch)
        bToggleSearch?.setOnClickListener(View.OnClickListener { scanLeDevice(!isScanning) })

        //Zugreifen auf Bluetooth LE Scanner Instanz
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        leScanner = bluetoothManager.adapter.bluetoothLeScanner
        if (leScanner == null) {
            Toast.makeText(this, R.string.bluetooth_not_available, Toast.LENGTH_LONG).show()
            setResult(RESULT_CANCELED)
            finish()
        } else {
            //Vorbereiten des List-Adapters
            leDeviceListAdapter = LeDeviceListAdapter(this)
            val listViewBLEDevices = findViewById<ListView>(R.id.lvBLEDevices)
            listViewBLEDevices.adapter = leDeviceListAdapter

            //Beim Klicken auf ein Item werden die Geräte Daten an die Parent-Activity zurückgegeben
            listViewBLEDevices.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, position, l ->
                    scanLeDevice(false)
                    val data = Intent()
                    data.putExtra(SELECTED_DEVICE, leDeviceListAdapter!!.getDevice(position))
                    setResult(RESULT_OK, data)
                    finish()
                }
            scanLeDevice(true)
        }
    }

    /**
     * Starten/Stoppen des Scannen nach BLE Geräten
     * Ein gestarteter Scan wird nach SCAN_PERIOD ms gestoppt
     *
     * @param enable true um Scan zu starten / false um zu Stoppen
     */
    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            //Löschen der Liste
            leDeviceListAdapter!!.clear()
            //Scan nach festgelegter Zeit stoppen
            handler!!.postDelayed(rTimeout, SCAN_PERIOD)
            isScanning = true
            //Ändern des Button Textes
            bToggleSearch!!.text = getString(R.string.stop_search)
            leScanner!!.startScan(leScanCallback)
        } else {
            handler!!.removeCallbacks(rTimeout)
            isScanning = false
            bToggleSearch!!.text = getString(R.string.start_search)
            leScanner!!.stopScan(leScanCallback)
        }
    }

    /**
     * Stoppt das Scannen
     */
    private val rTimeout = Runnable { scanLeDevice(false) }

    /**
     * Wird aufgerufen, wenn ein Bluetooth LE fähiges Gerät gefunden wurde
     */
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            runOnUiThread {
                leDeviceListAdapter!!.addDevice(result.device)
                leDeviceListAdapter!!.notifyDataSetChanged()
            }
        }
    }

    companion object {
        const val SELECTED_DEVICE = "SELECTED_DEVICE"

        //Scannen stoppen nach
        private const val SCAN_PERIOD: Long = 10000
    }
}