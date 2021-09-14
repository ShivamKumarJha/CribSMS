package com.shivamkumarjha.cribsms.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
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

        //Get matching SMS on device using Content resolver
        val uri: Uri = Uri.parse("content://sms/")
        val cursor: Cursor? = contentResolver.query(
            uri,
            null,
            "${Telephony.Sms.ADDRESS} LIKE ? AND ${Telephony.Sms.DATE} BETWEEN ? AND ?",
            arrayOf("%$phone%", "" + timeStart, "" + timeEnd),
            "${Telephony.Sms.DATE} DESC"
        )
        if (cursor != null) {
            val totalSMS: Int = cursor.count

            //Populate matching messages
            val cursorMessages: ArrayList<SMSData> = arrayListOf()
            if (cursor.moveToFirst()) {
                for (i in 0 until totalSMS) {
                    val type = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                    val folder = if (type.contains("1")) "inbox" else "sent"
                    val smsData = SMSData(
                        cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)),
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