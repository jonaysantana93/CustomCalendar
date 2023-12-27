package com.jonay.customcalendar.extensions

import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.SHORT
import java.util.Locale

private fun Calendar.customClone() = this.clone() as Calendar

fun Calendar.getTotalDaysInMonth(): Int = this.getActualMaximum(DAY_OF_MONTH)
fun Calendar.getDayOfWeek(day: Int): Int = (this.apply { set(DAY_OF_MONTH, day) }.get(DAY_OF_WEEK)) % 7
private fun Calendar.getFirstDayOfMonth(startDay: Int): Int {
    val calendarCopy = this.customClone()
    return calendarCopy.apply { set(DAY_OF_MONTH, 1) }.get(DAY_OF_WEEK) - startDay
}

fun Calendar.getRowPosition(startDay: Int, day: Int): Int {
    val calendarCopy = this.customClone()
    return (calendarCopy.getFirstDayOfMonth(startDay) + day -1)/7+1
}

fun Calendar.getNameDaysOfTheWeek(startDay: Int): List<String> {
    val calendarCopy = this.customClone()
    val daysNames = mutableListOf<String>()

    for (i in 0..6) {
        calendarCopy.set(DAY_OF_WEEK, (i+startDay)%7)
        calendarCopy.getDisplayName(DAY_OF_WEEK, SHORT, Locale.getDefault())?.let {
            daysNames.add(it.uppercase())
        }
    }

    return daysNames
}

fun Calendar.getNamesOfMonths(): List<String> {
    val list = mutableListOf<String>()
    val calendarCopy = this.customClone()
    val currentMonth = calendarCopy.get(Calendar.MONTH)

    for (month in currentMonth .. currentMonth+11){
        calendarCopy.set(Calendar.MONTH, month)
        calendarCopy.getDisplayName(Calendar.MONTH, SHORT, Locale.getDefault())?.let {
            list.add(it.capitalizeFirstCharacter())
        }
    }

    return list
}

fun Calendar.getNumberOfMonthWithName(monthName: String): Int {
    val calendarCopy = this.customClone()
    var monthNumber: Int = -1
    var found = false

    for (month in 0 until 12) {
        calendarCopy.apply { set(Calendar.MONTH, month) }.getDisplayName(Calendar.MONTH, SHORT, Locale.getDefault())?.let {
            if (it.lowercase() == monthName.lowercase()){
                monthNumber = calendarCopy.get(Calendar.MONTH)
                found = true
            }
        }

        if (found) break
    }

    return monthNumber
}

fun Calendar.getColumPosition(day: Int): Int {
    val calendarCopy = this.customClone()
    return (calendarCopy.apply { set(DAY_OF_MONTH,day) }.get(DAY_OF_WEEK)-1) %7
}
