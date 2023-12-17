package com.jonay.customcalendar.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val numberOfYears: Int = 100

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

fun Calendar.getListOfYears() : List<String> {
    val mutableList = mutableListOf<String>()

    val currentYear = this.get(Calendar.YEAR)

    for(year in currentYear until (currentYear+numberOfYears)) {
        mutableList.add(this.get(Calendar.YEAR).toString())
        this.add(Calendar.YEAR, 1)
    }

    return mutableList
}

fun Calendar.getListOfMonths(): List<String> {
    val mutableList = mutableListOf<String>()

    val currentYear = this.get(Calendar.YEAR)
    val currentMonth = this.get(Calendar.MONTH)

    for(year in currentYear until (currentYear + numberOfYears)) {
        if (year == currentYear){
            for (month in currentMonth..11) {
                this.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.let {
                    mutableList.add(it.substring(0..2))
                }
                this.add(Calendar.MONTH, 1)
            }
        } else {
            for (month in 0..11){
                this.set(Calendar.MONTH, month)
                this.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.let {
                    mutableList.add(it.substring(0..2))
                }
            }
        }
    }

    return mutableList
}