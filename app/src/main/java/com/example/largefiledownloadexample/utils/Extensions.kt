package com.example.largefiledownloadexample.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showMessage(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG)
        .show()
}

fun Activity.showMessage(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG)
        .show()
}