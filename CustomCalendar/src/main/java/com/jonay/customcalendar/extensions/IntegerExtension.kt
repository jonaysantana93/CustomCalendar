package com.jonay.customcalendar.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.jonay.customcalendar.CustomCalendarOptions
import com.jonay.customcalendar.databinding.CustomCalendarTextItemBinding
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

fun Int.checkIfIsCurrentDay(binding : CustomCalendarTextItemBinding, calendar : Calendar, currentDate : Calendar, options: CustomCalendarOptions): CustomCalendarTextItemBinding? =
    if (calendar.apply {  set(DAY_OF_MONTH, this@checkIfIsCurrentDay) }.checkCurrentDate(currentDate)) {
        binding.apply {
            dayContainer.background = options.currentDayCellCustomDesign.getDrawableResource(binding.root.context)
            dayText.setTextColor(options.dayTextColor.getResource(binding.root.context))
        }
    } else { null }

fun Int.checkIfIsaPassDay(binding: CustomCalendarTextItemBinding, calendar : Calendar, currentDate : Calendar, passDayLockTextColor: Int): Boolean {
    calendar.apply { set(DAY_OF_MONTH, this@checkIfIsaPassDay) }
    if (calendar.checkCurrentMonthAndYear(currentDate)) {
        val passDay = this < currentDate.get(DAY_OF_MONTH)
        if (passDay){
            binding.apply {
                dayText.setTextColor(passDayLockTextColor.getResource(binding.root.context))
                root.isClickable = false
            }
        }
        return !passDay
    }

//    val passDay = this < currentDay
//    if (passDay){
//        binding.apply {
//            dayText.setTextColor(passDayLockTextColor.getResource(binding.root.context))
//            root.isClickable = false
//        }
//    }
//
    return true
}

private fun Calendar.checkCurrentMonthAndYear(currentDate: Calendar): Boolean = this.get(MONTH) == currentDate.get(MONTH) && this.get(YEAR) == currentDate.get(YEAR)

private fun Calendar.checkCurrentDate(currentDate : Calendar): Boolean =
    this.get(DAY_OF_MONTH) == currentDate.get(DAY_OF_MONTH) &&
            this.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
            this.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)

fun Int.checkListOfEvents(listOfEvents : List<Int>, currentDay: Int) : Boolean = listOfEvents.any { it == this && this != currentDay }

fun Int.getResource(context : Context) : Int = ContextCompat.getColor(context, this)
fun Int.getDrawableResource(context : Context) : Drawable? = ContextCompat.getDrawable(context, this)

fun Int.getColumPosition(startDay: Int, dayOfWeek: Int): Int = (dayOfWeek - startDay + 7) % 7

//abs((calendar.apply { set(2023, Calendar.JANUARY,1) }.get(Calendar.DAY_OF_WEEK)-7)%7)