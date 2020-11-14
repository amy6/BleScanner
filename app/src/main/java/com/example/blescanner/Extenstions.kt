package com.example.blescanner

import android.content.Context
import android.widget.Toast

fun Context.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}