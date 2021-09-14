package com.shivamkumarjha.cribsms.ui.extensions

import android.content.Context
import android.widget.Toast

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()