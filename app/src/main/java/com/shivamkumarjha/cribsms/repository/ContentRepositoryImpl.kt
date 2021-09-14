package com.shivamkumarjha.cribsms.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import com.shivamkumarjha.cribsms.di.IoDispatcher
import com.shivamkumarjha.cribsms.model.SMSData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

class ContentRepositoryImpl(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ContentRepository {

    override suspend fun getSMS(phone: String, daysGap: Int): Flow<ArrayList<SMSData>?> = flow {
        //Date
        val dayMilliSeconds = (1000 * 60 * 60 * 24).toLong()
        val timeStart = Date(System.currentTimeMillis() - (daysGap * dayMilliSeconds)).time
        val timeEnd = Date(System.currentTimeMillis()).time

        //Get all SMS on device using Content resolver
        val uri: Uri = Uri.parse("content://sms/")
        val cursor: Cursor? = contentResolver.query(
            uri,
            null,
            "address LIKE ? AND date BETWEEN ? AND ?",
            arrayOf("%$phone%", "" + timeStart, "" + timeEnd),
            "date DESC"
        )
        if (cursor != null) {
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
            emit(cursorMessages)
        } else {
            emit(null)
        }
    }.flowOn(ioDispatcher)
}