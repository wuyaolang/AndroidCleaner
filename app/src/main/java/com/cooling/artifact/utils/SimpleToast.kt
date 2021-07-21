package com.cooling.artifact.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * 简易的Toast
 */
object SimpleToast {
    private var sToast: Toast? = null
    @SuppressLint("ShowToast")
    fun show(context: Context, text: String?) {
        if (sToast == null) {
            sToast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_SHORT)
        } else {
            sToast!!.setText(text)
        }
        sToast!!.show()
    }

    @SuppressLint("ShowToast")
    fun show(context: Context, @StringRes resId: Int) {
        if (sToast == null) {
            sToast = Toast.makeText(context.applicationContext, resId, Toast.LENGTH_SHORT)
        } else {
            sToast!!.setText(resId)
        }
        sToast!!.show()
    }

    @SuppressLint("ShowToast")
    fun showLong(context: Context, text: String?) {
        if (sToast == null) {
            sToast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG)
        } else {
            sToast!!.setText(text)
        }
        sToast!!.show()
    }

    @SuppressLint("ShowToast")
    fun showLong(context: Context, @StringRes resId: Int) {
        if (sToast == null) {
            sToast = Toast.makeText(context.applicationContext, resId, Toast.LENGTH_LONG)
        } else {
            sToast!!.setText(resId)
        }
        sToast!!.show()
    }

    fun cancel() {
        if (sToast != null) {
            sToast!!.cancel()
        }
    }
}