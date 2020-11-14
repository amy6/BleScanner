package com.example.blescanner

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

fun Activity.showAlert(
    title: String,
    message: String,
    isCancelable: Boolean = false,
    positiveBtnClickListener: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    negativeBtnClickListener: DialogInterface.OnClickListener? = null,
    cancelBtnClickListener: DialogInterface.OnCancelListener? = null,
    dismissBtnClickListener: DialogInterface.OnDismissListener? = null
) {
    val builder = AlertDialog.Builder(this)

    with(builder) {
        setTitle(title)
        setMessage(message)
        setCancelable(isCancelable)
        positiveBtnClickListener?.let {
            setPositiveButton(
                android.R.string.ok,
                DialogInterface.OnClickListener(function = positiveBtnClickListener)
            )
        }

        negativeBtnClickListener?.let { setNegativeButton(android.R.string.no, it) }

        if (isCancelable) cancelBtnClickListener?.let { setOnCancelListener(it) }

        dismissBtnClickListener?.let { setOnDismissListener(it) }
        show()
    }
}