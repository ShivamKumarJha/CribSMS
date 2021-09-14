package com.shivamkumarjha.cribsms.model

data class SMSData(
    val id: String,
    val address: String,
    val message: String,
    val time: String,
    val folder: String,
)