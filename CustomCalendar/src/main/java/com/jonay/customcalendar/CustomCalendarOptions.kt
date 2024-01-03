package com.jonay.customcalendar

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.jonay.customcalendar.enums.Months
import com.jonay.customcalendar.enums.StartDayOfWeek
import java.util.Calendar

class CustomCalendarOptions {
//    val startDay: StartDayOfWeek = StartDayOfWeek.SUNDAY
    var month: Months = Months.getCurrentMonth()
//    var year: Int = Calendar.getInstance().get(Calendar.YEAR)
    var listOfEvents: List<Int> = listOf()
    @ColorRes var dayCellBackground:Int = R.color.white
    @ColorRes var dayTextColor: Int = R.color.black
    @ColorRes var dayCellBackgroundWithEvent: Int = R.color.green
    @ColorRes var dayTextColorWithEvent: Int = R.color.white
    @ColorRes var passDayLockTextColor: Int = R.color.light_gray
    @DrawableRes var currentDayCellCustomDesign: Int = R.drawable.cardview_border
}