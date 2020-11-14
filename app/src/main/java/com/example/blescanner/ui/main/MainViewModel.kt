package com.example.blescanner.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.blescanner.data.ble.IBleManager
import com.example.blescanner.data.model.BleDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

class MainViewModel(private val bleManager: IBleManager) : ViewModel() {

    var scanFlow: Flow<BleDevice> = bleManager.scanChannel.consumeAsFlow()

    var isScanning = bleManager.isScanning.asLiveData()

    fun toggleScan() {
        bleManager.apply { if (isScanning.value) stopScan() else startScan() }
    }
}