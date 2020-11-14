package com.example.blescanner.ui.main

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blescanner.R
import com.example.blescanner.toastShort
import com.example.blescanner.utils.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val REQUEST_ENABLE_BT = 32001
private const val REQUEST_LOCATION_SERVICE = 32002
private const val PERMISSION_REQUEST_FINE_LOCATION = 32003

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val bluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private var locationServiceEnabled: CompletableDeferred<Boolean>? = null
    private var locationPermissionGranted: CompletableDeferred<Boolean>? = null
    private var bluetoothEnabled: CompletableDeferred<Boolean>? = null

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        launch {
            if (initializeBleScanner())
                withContext(Dispatchers.Main) { toastShort("Scanner initialised successfully") }
            else
                withContext(Dispatchers.Main) { toastShort("Scanner initialisation failed") }
        }

    }

    private suspend fun initializeBleScanner(): Boolean {
        locationServiceEnabled = CompletableDeferred()
        locationPermissionGranted = CompletableDeferred()
        bluetoothEnabled = CompletableDeferred()

        fun done(result: Boolean = false): Boolean {
            locationServiceEnabled = null
            locationPermissionGranted = null
            bluetoothEnabled = null
            return result
        }

        if (!hasPermission(PERMISSION_TYPE_FINE_LOCATION)) {
            val positiveButtonClick = { _: DialogInterface, _: Int ->
                requestPermission(
                    PERMISSION_TYPE_FINE_LOCATION,
                    PERMISSION_REQUEST_FINE_LOCATION
                )
            }
            withContext(Dispatchers.Main) {
                showAlert(
                    title = "Location Permission Required",
                    message = "The application requires location access in order to scan for BLE devices.",
                    positiveBtnClickListener = positiveButtonClick
                )
            }
            if (locationPermissionGranted?.await() == false) return done()
        }

        if (!isLocationServiceEnabled()) {
            promptEnableLocationServices(REQUEST_LOCATION_SERVICE)
            if (locationServiceEnabled?.await() == false) return done()
        }

        if (bluetoothAdapter.isDisabled) {
            promptEnableBluetooth(REQUEST_ENABLE_BT)
            if (bluetoothEnabled?.await() == false) return done()
        }

        return done(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            REQUEST_ENABLE_BT -> bluetoothEnabled?.run {
                complete(resultCode == RESULT_OK)
            }

            REQUEST_LOCATION_SERVICE -> locationServiceEnabled?.run {
                complete(isLocationServiceEnabled())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            PERMISSION_REQUEST_FINE_LOCATION -> locationPermissionGranted?.run {
                complete(grantResults.contains(PackageManager.PERMISSION_GRANTED))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
