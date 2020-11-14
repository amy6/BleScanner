package com.example.blescanner.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blescanner.data.ble.IBleManager

class MainViewModelFactory(private val bleManager: IBleManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(bleManager) as T
        throw IllegalArgumentException("Unable to create ViewModel)")
    }
}