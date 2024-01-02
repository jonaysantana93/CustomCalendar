package com.jonay.customcalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CustomCalendarViewModel: ViewModel() {

    private val _eventsList = Channel<List<Int>?>(Channel.BUFFERED)
    val eventList = _eventsList.receiveAsFlow()

    fun updateListOfEvents(list: List<Int>) = viewModelScope.launch {
        _eventsList.send(list)
    }

}