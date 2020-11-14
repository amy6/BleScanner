package com.example.blescanner.data.ble

import com.example.blescanner.data.model.BleDevice
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BleManager(private val bleScanner: BleScanner) : IBleManager {

    private var _isScanning = MutableStateFlow(false)
    override var isScanning: StateFlow<Boolean> = _isScanning

    override fun startScan() {
        _isScanning.value = true
        bleScanner.startScan()
    }

    override fun stopScan() {
        _isScanning.value = false
        bleScanner.stopScan()
    }

    override var scanChannel: Channel<BleDevice> = bleScanner.channel
}