package com.shivamkumarjha.cribsms.ui.home

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shivamkumarjha.cribsms.R
import com.shivamkumarjha.cribsms.model.SMSData
import java.util.*


class HomeViewModel : ViewModel() {

    private val _formState = MutableLiveData<HomeFormState>()
    val formState: LiveData<HomeFormState> = _formState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _messages = MutableLiveData<ArrayList<SMSData>>()
    val messages: LiveData<ArrayList<SMSData>> = _messages

    init {
        _isLoading.postValue(false)
    }

    fun inputValidator(phone: String, days: Int?) {
        if (!Patterns.PHONE.matcher(phone).matches()) {
            _formState.value =
                HomeFormState(phoneError = R.string.invalid_phone_number, isInputValid = false)
        } else if (days == null || days <= 0) {
            _formState.value =
                HomeFormState(daysError = R.string.invalid_days, isInputValid = false)
        } else {
            _formState.value = HomeFormState(isInputValid = true)
        }
    }

    fun getSMS(contentResolver: ContentResolver, phone: String, daysGap: Int) {
        _isLoading.postValue(true)

        //Date
        val dayMilliSeconds = (1000 * 60 * 60 * 24).toLong()
        val timeStart = Date(System.currentTimeMillis() - (daysGap * dayMilliSeconds)).time
        val timeEnd = Date(System.currentTimeMillis()).time

        //Get all SMS on device using Content resolver
        val uri: Uri = Uri.parse("content://sms/")
        val cursor: Cursor = contentResolver.query(
            uri,
            null,
            "address LIKE ? AND date BETWEEN ? AND ?",
            arrayOf("%$phone%", "" + timeStart, "" + timeEnd),
            "date DESC"
        ) ?: return
        val totalSMS: Int = cursor.count

        //Populate all messages
        val cursorMessages: ArrayList<SMSData> = arrayListOf()
        if (cursor.moveToFirst()) {
            for (i in 0 until totalSMS) {
                val folder =
                    if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains("1")) {
                        "inbox"
                    } else {
                        "sent"
                    }
                val smsData = SMSData(
                    cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    cursor.getString(cursor.getColumnIndexOrThrow("body")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    folder
                )
                cursorMessages.add(smsData)
                cursor.moveToNext()
            }
        }
        cursor.close()
        _messages.postValue(cursorMessages)

        _isLoading.postValue(false)
    }
}