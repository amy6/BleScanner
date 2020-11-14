package com.example.blescanner.data.ble

import com.example.blescanner.data.model.BleDevice
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface IBleManager {
    fun startScan()
    fun stopScan()
    var isScanning: StateFlow<Boolean>
    var scanChannel: Channel<BleDevice>
}