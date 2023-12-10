package com.jonay.customcalendar.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Calendar.getTotalDaysInMonth(): Int = this.getActualMaximum(Calendar.DAY_OF_MONTH)
fun Calendar.getFirstDayOfMonth(startDay: Int): Int = this.apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK) - startDay

fun Calendar.getNameDaysOfTheWeek(): List<String> {
    val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    val daysNames = mutableListOf<String>()

    for (i in 1..7) {
        this.set(Calendar.DAY_OF_WEEK, i)
        val name = simpleDateFormat.format(this.time)
        daysNames.add(name.substring(0, 3).uppercase())
    }

    return daysNames
}