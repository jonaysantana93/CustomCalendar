package com.jonay.customcalendar.extensions

import android.content.Context
import androidx.core.content.ContextCompat
import com.jonay.customcalendar.databinding.CustomCalendarTextItemBinding

fun Int.checkIfIsaPassDay(binding: CustomCalendarTextItemBinding, currentDay: Int, passDayLockTextColor: Int): Boolean {
    val passDay = this < currentDay
    if (passDay){
        binding.apply {
            dayText.setTextColor(passDayLockTextColor.getResource(binding.root.context))
            root.isClickable = false
        }
    }

    return !passDay
}

fun Int.checkListOfEvents(listOfEvents : List<Int>) : Boolean = listOfEvents.any { it == this }

fun Int.getResource(context : Context) : Int = ContextCompat.getColor(context, this)