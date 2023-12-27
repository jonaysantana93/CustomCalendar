package com.jonay.customcalendar.extensions

import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.SHORT
import java.util.Locale

fun Calendar.getTotalDaysInMonth(): Int = this.getActualMaximum(DAY_OF_MONTH)
fun Calendar.getDayOfWeek(day: Int): Int = (this.apply { set(DAY_OF_MONTH, day) }.get(DAY_OF_WEEK)) % 7

fun Calendar.getNameDaysOfTheWeek(startDay: Int): List<String> {
    val daysNames = mutableListOf<String>()

    for (i in 0..6) {
        this.set(DAY_OF_WEEK, (i+startDay)%7)
        this.getDisplayName(DAY_OF_WEEK, SHORT, Locale.getDefault())?.let {
            daysNames.add(it.uppercase())
        }
    }

    return daysNames
}

fun Calendar.getNamesOfMonths(): List<String> {
    val list = mutableListOf<String>()
    val currentMonth = this.get(Calendar.MONTH)

    for (month in currentMonth .. currentMonth+11){
        this.set(Calendar.MONTH, month)
        this.getDisplayName(Calendar.MONTH, SHORT, Locale.getDefault())?.let {
            list.add(it.capitalizeFirstCharacter())
        }
    }

    return list
}

fun Calendar.getNumberOfMonthWithName(monthName: String): Int {
    var monthNumber: Int = -1

    for (mes in 0 until 12) {
        this.set(Calendar.MONTH, mes)
        this.getDisplayName(Calendar.MONTH, SHORT, Locale.getDefault())?.let {
            if (it.lowercase() == monthName.lowercase()){
                monthNumber = mes
            }
        }
    }

    return monthNumber
}
