package com.shivamkumarjha.cribsms.repository

import com.shivamkumarjha.cribsms.model.SMSData
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    suspend fun getSMS(phone: String, daysGap: Int): Flow<ArrayList<SMSData>?>
}