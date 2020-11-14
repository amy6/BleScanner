package com.example.blescanner.data.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import com.example.blescanner.data.model.BleDevice
import kotlinx.coroutines.channels.Channel

class BleScanner(
    private val bluetoothLeScanner: BluetoothLeScanner,
    private val scanFilters: List<ScanFilter>? = null,
    private val scanSettings: ScanSettings = ScanSettings.Builder().build()
) {

    val channel = Channel<BleDevice>()

    fun startScan() {
        bluetoothLeScanner.startScan(scanFilters, scanSettings, scanChannelCallback)
    }

    fun stopScan() {
        bluetoothLeScanner.stopScan(scanChannelCallback)
    }

    private val scanChannelCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            result.device?.let { device: BluetoothDevice ->
                channel.offer(BleDevice(device.name, device.address, result.rssi))
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result: ScanResult ->
                result.device?.let { device: BluetoothDevice ->
                    channel.offer(
                        BleDevice(
                            device.name,
                            device.address,
                            result.rssi
                        )
                    )
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            channel.close(ScanFailedException(errorCode))
        }
    }
}