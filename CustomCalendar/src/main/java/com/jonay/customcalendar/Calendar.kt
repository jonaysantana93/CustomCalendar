package com.jonay.customcalendar

import androidx.fragment.app.FragmentActivity

private const val CALENDAR_TAG = "CALENDAR_TAG"

class Calendar(
    appActivity: FragmentActivity,
    container: Int,
    options: CustomCalendarOptions? = null
) {

    var onDayClick: ((day: Int) -> Unit)? = null
    var updateListEvents: ((month: Int, year: Int) -> Unit)? = null

    private lateinit var customCalendar: CustomCalendar

    companion object {
        lateinit var configOptions: CustomCalendarOptions
    }

    init {
        configOptions = options ?: CustomCalendarOptions()
        initializeCustomCalendar(appActivity, container)
    }

    private fun initializeCustomCalendar(appActivity: FragmentActivity, container: Int) {
        customCalendar = CustomCalendar(
            context = appActivity,
            onClick = { onDayClick?.invoke(it) },
            updateEvents = { month, year -> updateListEvents?.invoke(month, year) }
        )

        appActivity.supportFragmentManager.beginTransaction()
            .replace(container, customCalendar, CALENDAR_TAG).commit()
    }

    fun updateListOfEvents(list: List<Int>) = customCalendar.updateEvents(list)
}