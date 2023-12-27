package com.jonay.customcalendar.enums

import java.util.Calendar

enum class Months(val value: Int) {
    JANUARY(Calendar.JANUARY),
    FEBRUARY(Calendar.FEBRUARY),
    MARCH(Calendar.MARCH),
    APRIL(Calendar.APRIL),
    MAY(Calendar.MAY),
    JUNE(Calendar.JUNE),
    JULY(Calendar.JULY),
    AUGUST(Calendar.AUGUST),
    SEPTEMBER(Calendar.SEPTEMBER),
    OCTOBER(Calendar.OCTOBER),
    NOVEMBER(Calendar.NOVEMBER),
    DECEMBER(Calendar.DECEMBER);

    companion object {
        fun getCurrentMonth(): Months = entries.first { it.value == Calendar.getInstance().get(Calendar.MONTH) }
        fun getCustomMonth(month: Int) = entries.first { it.value == month }
    }
}