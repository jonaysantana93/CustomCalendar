package com.jonay.customcalendar.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.jonay.customcalendar.databinding.CustomCalendarTextItemBinding
import java.util.Calendar

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

fun Int.checkListOfEvents(listOfEvents : List<Int>, currentDay: Int) : Boolean = listOfEvents.any { it == this && this != currentDay }

fun Int.getResource(context : Context) : Int = ContextCompat.getColor(context, this)
fun Int.getDrawableResource(context : Context) : Drawable? = ContextCompat.getDrawable(context, this)

fun Int.getColumPosition(startDay: Int, dayOfWeek: Int): Int = (dayOfWeek - startDay + 7) % 7

//abs((calendar.apply { set(2023, Calendar.JANUARY,1) }.get(Calendar.DAY_OF_WEEK)-7)%7)