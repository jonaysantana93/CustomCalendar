package com.jonay.customcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.jonay.customcalendar.adapter.month.MonthAdapter
import com.jonay.customcalendar.adapter.year.YearAdapter
import com.jonay.customcalendar.common.utils.viewBinding.viewBinding
import com.jonay.customcalendar.databinding.CustomCalendarTextItemBinding
import com.jonay.customcalendar.databinding.FragmentCustomCalendarBinding
import com.jonay.customcalendar.enums.Months
import com.jonay.customcalendar.enums.StartDayOfWeek
import com.jonay.customcalendar.extensions.checkIfIsaPassDay
import com.jonay.customcalendar.extensions.checkListOfEvents
import com.jonay.customcalendar.extensions.getFirstDayOfMonth
import com.jonay.customcalendar.extensions.getNameDaysOfTheWeek
import com.jonay.customcalendar.extensions.getNamesOfMonths
import com.jonay.customcalendar.extensions.getResource
import com.jonay.customcalendar.extensions.getTotalDaysInMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CustomCalendar(
    private val context: Context,
    private val startDay: StartDayOfWeek = StartDayOfWeek.SUNDAY,
    private val month: Months = Months.getCurrentMonth(),
    private val listOfEvents: List<Int> = listOf()
) : Fragment(R.layout.fragment_custom_calendar) {

    private val binding by viewBinding(FragmentCustomCalendarBinding::bind)

    var onClick: ((day: Int) -> Unit)? = null

    private var calendar: Calendar = Calendar.getInstance().apply { set(Calendar.MONTH, month.value) }
    private var cellWidth = context.resources.getDimensionPixelSize(R.dimen.grid_cell_width)
    private var cellHeight = context.resources.getDimensionPixelSize(R.dimen.grid_cell_height)
    private val currentDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var daySelected: Int? = currentDay
    private var daySelectedView: CustomCalendarTextItemBinding? = null


// -----------------------------------------
// ------ CUSTOMIZABLE VARIABLE BLOCK ------
// -----------------------------------------

    private var dayCellBackground:Int = R.color.white
    fun setDayCellBackgroundColor(value: Int) {
        dayCellBackground = value
    }

    private var dayTextColor: Int = R.color.black
    fun setDayTextColor(value: Int){
        dayTextColor = value
    }

    private var dayCellBackgroundWithEvent: Int = R.color.green
    fun setDayCellBackgroundWithEvent(value: Int) {
        dayCellBackgroundWithEvent = value
    }

    private var dayTextColorWithEvent: Int = R.color.white
    fun setDayTextColorWithEvent(value: Int) {
        dayTextColorWithEvent = value
    }

    private var passDayLockTextColor: Int = R.color.light_gray
    fun setPassDayLockTextColor(value: Int){
        passDayLockTextColor = value
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private var currentDayCellCustomDesign: Drawable? = context.getDrawable(R.drawable.cardview_border)
    fun setCurrentDayCellBackgroundColor(design: Drawable? = null) {
        design?.let {
            currentDayCellCustomDesign = design
        }
    }
// -----------------------------------------
// -----------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendarView()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initCalendarView() {
        buildMonthsRecyclerView()
        buildYearRecyclerView()
        binding.monthName.text = SimpleDateFormat("MMMM").format(calendar.time)
        buildDaysOfWeeks()
        buildAllDaysOfMonths()
    }

    private fun buildMonthsRecyclerView() = binding.apply {
        val customAdapter = MonthAdapter()

        monthsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = customAdapter
        }
    }

    private fun buildYearRecyclerView() = binding.apply {
        val customAdapter = YearAdapter()

        yearsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = customAdapter
        }
    }

    private fun buildDaysOfWeeks() {
        val daysOfWeeks = calendar.getNameDaysOfTheWeek()
        daysOfWeeks.forEachIndexed { i, name ->
            val textView = TextView(context).apply {
                text = name
                gravity = Gravity.CENTER
                width = cellWidth
                height = cellHeight
            }

            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(0, 1)
                columnSpec = GridLayout.spec(i, 1)
                width = cellWidth
                height = cellHeight
                setGravity(Gravity.FILL)
            }

            binding.gridCalendar.addView(textView, params)
        }
    }

    private fun buildAllDaysOfMonths() {
        val firstDay = calendar.getFirstDayOfMonth(startDay.value)
        val totalDays = calendar.getTotalDaysInMonth()

        for (day in 1..totalDays) {
            val column = (firstDay + day - 1) % 7
            val row = (firstDay + day - 1) / 7 + 1

            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                width = cellWidth
                height = cellHeight
                setGravity(Gravity.FILL)
                columnSpec = GridLayout.spec(column, 1f)
                rowSpec = GridLayout.spec(row, 1f)
            }

            val view = day.buildCustomView()
            binding.gridCalendar.addView(view, params)
        }
    }

    private fun Int.buildCustomView(): View =
        CustomCalendarTextItemBinding.inflate(LayoutInflater.from(context)).apply {
            val day = this@buildCustomView

            dayContainer.setCardBackgroundColor(dayCellBackground.getResource(context))
            dayText.apply {
                text = day.toString()
                setTextColor(dayTextColor.getResource(context))
            }

            if (day.checkIfIsaPassDay(this, currentDay, passDayLockTextColor)) {
                day.checkIfIsCurrentDay(this)
                day.checkIfHaveEvent(this)
                root.onclickListener(day, this)
            }
        }.root

    private fun Int.checkIfIsCurrentDay(binding: CustomCalendarTextItemBinding) {
        if (this == currentDay) {
            binding.dayContainer.background = currentDayCellCustomDesign
            daySelectedView = binding
        }
    }

    private fun Int.checkIfHaveEvent(binding : CustomCalendarTextItemBinding) {
        if(this.checkListOfEvents(listOfEvents)){
            binding.apply {
                dayContainer.setCardBackgroundColor(dayCellBackgroundWithEvent.getResource(context))
                dayText.setTextColor(dayTextColorWithEvent.getResource(context))
            }
        }
    }

    private fun ConstraintLayout.onclickListener(day: Int, binding: CustomCalendarTextItemBinding) {
        this.setOnClickListener {
            if (day >= currentDay) {
                clearOldSelectedDay()
                binding.updateSelectedDay(day)
                onClick?.invoke(day)
            }
        }
    }

    private fun clearOldSelectedDay() {
        daySelected?.let {
            daySelectedView?.checkLisOfEvents(it)
        }
        daySelectedView = null
        daySelected = null
    }

    private fun CustomCalendarTextItemBinding.checkLisOfEvents(day: Int) = this.apply {
        if (day == currentDay){
            dayContainer.background = ContextCompat.getDrawable(context, R.drawable.cardview_border)
            dayText.setTextColor(R.color.black.getResource(context))
            daySelectedView = this
        } else if (day.checkListOfEvents(listOfEvents)){
            dayContainer.setCardBackgroundColor(dayCellBackgroundWithEvent.getResource(context))
            dayText.setTextColor(dayTextColorWithEvent.getResource(context))
        } else  {
            dayContainer.setCardBackgroundColor(R.color.white.getResource(context))
            dayText.setTextColor(R.color.black.getResource(context))
        }
    }

    private fun CustomCalendarTextItemBinding.updateSelectedDay(day: Int) {
        if (day != currentDay){
            daySelectedView = this.apply {
                dayContainer.setCardBackgroundColor(R.color.light_gray.getResource(context))
                dayText.setTextColor(R.color.white.getResource(context))
            }
        }
        daySelected = day
    }

//    private fun Calendar.getNamesOfMonths(): List<String> {
//        val list = mutableListOf<String>()
//        val currentMonth = this.get(Calendar.MONTH)
//
//        //We get the months using the current date
//        for (month in currentMonth .. Calendar.DECEMBER){
//            this.set(Calendar.MONTH, month)
//            this.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.let {
//                list.add(it)
//            }
//        }
//
//        //we get all months 100 time
//        for (i in 0..100) {
//            for (month in Calendar.JANUARY .. Calendar.DECEMBER){
//                this.set(Calendar.MONTH, month)
//                this.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.let {
//                    list.add(it)
//                }
//            }
//        }
//
//        return list
//    }

//    private fun Calendar.getYearsList(futureYears: Int = 100): List<String> {
//        val listYears = mutableListOf<String>()
//        val currentYear = this.get(Calendar.YEAR)
//
//        for (year in currentYear..(currentYear+futureYears)){
//            listYears.add(year.toString())
//        }
//
//        return listYears
//    }
}


