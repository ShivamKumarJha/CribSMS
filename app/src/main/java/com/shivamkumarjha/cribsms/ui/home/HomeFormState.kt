package com.shivamkumarjha.cribsms.ui.home

data class HomeFormState(
    val phoneError: Int? = null,
    val daysError: Int? = null,
    val isInputValid: Boolean
)