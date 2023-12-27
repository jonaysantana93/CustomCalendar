package com.jonay.customcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonay.customcalendar.adapter.month.MonthAdapter
import com.jonay.customcalendar.adapter.year.YearAdapter
import com.jonay.customcalendar.common.utils.viewBinding.viewBinding
import com.jonay.customcalendar.databinding.CustomCalendarTextItemBinding
import com.jonay.customcalendar.databinding.FragmentCustomCalendarBinding
import com.jonay.customcalendar.enums.Months
import com.jonay.customcalendar.enums.StartDayOfWeek
import com.jonay.customcalendar.extensions.checkIfIsaPassDay
import com.jonay.customcalendar.extensions.checkListOfEvents
import com.jonay.customcalendar.extensions.getColumPosition
import com.jonay.customcalendar.extensions.getDrawableResource
import com.jonay.customcalendar.extensions.getNameDaysOfTheWeek
import com.jonay.customcalendar.extensions.getResource
import com.jonay.customcalendar.extensions.getRowPosition
import com.jonay.customcalendar.extensions.getTotalDaysInMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR


class CustomCalendar(
    private val context: Context,
    private val options: CustomCalendarOptions = CustomCalendarOptions(),
    private val listOfEvents: List<Int> = listOf()
) : Fragment(R.layout.fragment_custom_calendar) {

    private val binding by viewBinding(FragmentCustomCalendarBinding::bind)

    var onClick: ((day: Int) -> Unit)? = null

    //TMP
    private val startDay: StartDayOfWeek = StartDayOfWeek.SUNDAY

    private val calendar: Calendar by lazy { Calendar.getInstance().apply {
            set(MONTH, options.month.value)
            set(YEAR, options.year)
        }
    }
    private var cellWidth = context.resources.getDimensionPixelSize(R.dimen.grid_cell_width)
    private var cellHeight = context.resources.getDimensionPixelSize(R.dimen.grid_cell_height)
    private val currentDay: Int by lazy { Calendar.getInstance().get(DAY_OF_MONTH) }
    private var daySelected: Int? = currentDay
    private var daySelectedView: CustomCalendarTextItemBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildYearRecyclerView()
        buildMonthsRecyclerView()
        initCalendarView()
    }

    private fun buildYearRecyclerView() = binding.apply {
        val customAdapter = YearAdapter().apply {
            onClick = {
                options.year = it
                calendar.set(YEAR, options.year)
                initCalendarView()
            }
        }

        yearsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = customAdapter
        }
    }

    private fun buildMonthsRecyclerView() = binding.apply {
        val customAdapter = MonthAdapter().apply {
            onClick = {
                options.month = Months.getCustomMonth(it)
                calendar.set(MONTH, options.month.value)
                initCalendarView()
            }
        }

        monthsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = customAdapter
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initCalendarView() {
        binding.apply {
            monthName.text = SimpleDateFormat("MMMM").format(calendar.time)
            gridCalendar.removeAllViews()
        }
        buildDaysOfWeeks()
        buildAllDaysOfMonths()
    }

    private fun buildDaysOfWeeks() {
        val daysOfWeeks = calendar.getNameDaysOfTheWeek(startDay.value)
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
        for (day in 1..calendar.getTotalDaysInMonth()) {
            val column = calendar.getColumPosition(day)
            val row = calendar.getRowPosition(startDay.value, day)

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

            dayContainer.setCardBackgroundColor(options.dayCellBackground.getResource(context))
            dayText.apply {
                text = day.toString()
                setTextColor(options.dayTextColor.getResource(context))
            }

            if (day.checkIfIsaPassDay(this, currentDay, options.passDayLockTextColor)) {
                day.checkIfIsCurrentDay(this)
                day.checkIfHaveEvent(this)
                root.onclickListener(day, this)
            }
        }.root

    private fun Int.checkIfIsCurrentDay(binding: CustomCalendarTextItemBinding) {
        if (this == currentDay) {
            binding.dayContainer.background = options.currentDayCellCustomDesign.getDrawableResource(context)
            binding.dayText.setTextColor(options.dayTextColor.getResource(context))
            daySelectedView = binding
        }
    }

    private fun Int.checkIfHaveEvent(binding : CustomCalendarTextItemBinding) {
        if(this.checkListOfEvents(listOfEvents, currentDay)){
            binding.apply {
                dayContainer.setCardBackgroundColor(options.dayCellBackgroundWithEvent.getResource(context))
                dayText.setTextColor(options.dayTextColorWithEvent.getResource(context))
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
        } else if (day.checkListOfEvents(listOfEvents, currentDay)){
            dayContainer.setCardBackgroundColor(options.dayCellBackgroundWithEvent.getResource(context))
            dayText.setTextColor(options.dayTextColorWithEvent.getResource(context))
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


