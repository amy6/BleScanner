package com.example.blescanner.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import timber.log.Timber

/**
 * Extension functions to be used by Activities when requesting for a run-time permission
 */

const val PERMISSION_TYPE_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

fun Context.hasPermission(permissionType: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permissionType
    ) == PackageManager.PERMISSION_GRANTED

fun Activity.requestPermission(permissionType: String, requestCode: Int) =
    ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)

fun Activity.promptEnableBluetooth(requestCode: Int) {
    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    startActivityForResult(enableBtIntent, requestCode)
}

fun Context.isLocationServiceEnabled(): Boolean {
    val locationManager = getSystemService(LocationManager::class.java) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

// prompt to enable GPS without requiring the user to navigate to the device settings
fun Activity.promptEnableLocationServices(requestCode: Int): Boolean {
    var isGpsEnabled = false

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(createLocationRequest())
    val client: SettingsClient = LocationServices.getSettingsClient(this)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        isGpsEnabled = true
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                exception.startResolutionForResult(
                    this,
                    requestCode
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                Timber.e("promptEnableGPS: Failed to enable GPS")
            }
        }
    }

    return isGpsEnabled
}

private fun createLocationRequest(): LocationRequest = LocationRequest.create().apply {
    interval = 10000
    fastestInterval = 5000
    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
}