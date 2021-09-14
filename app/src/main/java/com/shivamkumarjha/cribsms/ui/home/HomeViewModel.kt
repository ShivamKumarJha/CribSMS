package com.shivamkumarjha.cribsms.ui.home

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivamkumarjha.cribsms.R
import com.shivamkumarjha.cribsms.di.IoDispatcher
import com.shivamkumarjha.cribsms.model.SMSData
import com.shivamkumarjha.cribsms.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _formState = MutableLiveData<HomeFormState>()
    val formState: LiveData<HomeFormState> = _formState

    private val _messages = MutableLiveData<ArrayList<SMSData>>()
    val messages: LiveData<ArrayList<SMSData>> = _messages

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

    fun getSMS(phone: String, daysGap: Int) {
        viewModelScope.launch(ioDispatcher) {
            contentRepository.getSMS(phone, daysGap).collect {
                _messages.postValue(it)
            }
        }
    }
}