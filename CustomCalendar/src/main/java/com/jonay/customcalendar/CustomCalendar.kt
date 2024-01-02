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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonay.customcalendar.adapter.month.MonthAdapter
import com.jonay.customcalendar.common.utils.viewBinding.viewBinding
import com.jonay.customcalendar.databinding.CustomCalendarTextItemBinding
import com.jonay.customcalendar.databinding.FragmentCustomCalendarBinding
import com.jonay.customcalendar.enums.Months
import com.jonay.customcalendar.enums.StartDayOfWeek
import com.jonay.customcalendar.extensions.checkIfIsCurrentDay
import com.jonay.customcalendar.extensions.checkIfIsaPassDay
import com.jonay.customcalendar.extensions.checkListOfEvents
import com.jonay.customcalendar.extensions.customClone
import com.jonay.customcalendar.extensions.getColumPosition
import com.jonay.customcalendar.extensions.getNameDaysOfTheWeek
import com.jonay.customcalendar.extensions.getResource
import com.jonay.customcalendar.extensions.getRowPosition
import com.jonay.customcalendar.extensions.getTotalDaysInMonth
import com.jonay.customcalendar.extensions.repeatOnLifecycleStarted
import com.jonay.customcalendar.viewmodel.CustomCalendarViewModel
import com.jonay.customcalendar.Calendar as CCalendar
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.LONG
import java.util.Calendar.MONTH
import java.util.Calendar.SHORT
import java.util.Calendar.YEAR
import java.util.Locale

class CustomCalendar(
    private val context: Context,
    private var onClick: ((day: Int) -> Unit)? = null,
    private var updateEvents: ((month: Int, year: Int) -> Unit)? = null
) : Fragment(R.layout.fragment_custom_calendar) {

    //TMP
    private val startDay: StartDayOfWeek = StartDayOfWeek.SUNDAY

    fun updateEvents(list: List<Int>) = viewModel.updateListOfEvents(list)

    private val binding by viewBinding(FragmentCustomCalendarBinding::bind)
    private val viewModel: CustomCalendarViewModel by viewModels()
    private val options: CustomCalendarOptions = CCalendar.configOptions

    private val calendar: Calendar by lazy { Calendar.getInstance().apply {
            set(MONTH, options.month.value)
            set(YEAR, options.year)
        }
    }
    private var cellWidth = context.resources.getDimensionPixelSize(R.dimen.grid_cell_width)
    private var cellHeight = context.resources.getDimensionPixelSize(R.dimen.grid_cell_height)
    private val currentDate by lazy { Calendar.getInstance() }
    private val currentDay: Int by lazy { currentDate.get(DAY_OF_MONTH) }
    private var daySelected: Int? = currentDay
    private var daySelectedView: CustomCalendarTextItemBinding? = null
    private var customMonthAdapter: MonthAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserInterfaceListener()
        buildMonthsRecyclerView()
        initCalendarView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUserInterfaceListener() {
        repeatOnLifecycleStarted {
            viewModel.eventList.collect { list ->
                list?.let {
                    options.listOfEvents = it
                    customMonthAdapter?.notifyDataSetChanged()
                    initCalendarView()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun buildMonthsRecyclerView() = binding.apply {
        yearText.text = calendar.get(YEAR).toString()

        calendar.getDisplayName(MONTH, SHORT, Locale.getDefault())?.let { currentMonth ->
            customMonthAdapter = MonthAdapter(currentMonth).apply {
                onClick = { month, position ->
                    options.month = Months.getCustomMonth(month)
                    calendar.set(MONTH, options.month.value)
                    selectedPosition = position
                    updateEvents?.invoke(month, (calendar.get(YEAR)+position))
                }
            }

            monthsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = customMonthAdapter
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initCalendarView() {
        // We need delete this
        binding.monthName.text = String.format("%s - %s", calendar.getDisplayName(MONTH, LONG, Locale.getDefault()), calendar.get(YEAR))
        // --------------------
        binding.gridCalendar.removeAllViews()
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

            if (day.checkIfIsaPassDay(this, calendar.customClone(), currentDate, options.passDayLockTextColor)) {
                day.checkIfIsCurrentDay(this, calendar.customClone(), currentDate, options)?.let { daySelectedView = it }
                day.checkIfHaveEvent(this)
                root.onclickListener(day, this)
            }
        }.root

    private fun Int.checkIfHaveEvent(binding : CustomCalendarTextItemBinding) {
        if(this.checkListOfEvents(options.listOfEvents, currentDay)){
            binding.apply {
                dayContainer.setCardBackgroundColor(options.dayCellBackgroundWithEvent.getResource(context))
                dayText.setTextColor(options.dayTextColorWithEvent.getResource(context))
            }
        }
    }

    private fun ConstraintLayout.onclickListener(day: Int, binding: CustomCalendarTextItemBinding) {
        this.setOnClickListener {
            clearOldSelectedDay()
            binding.updateSelectedDay(day)
            onClick?.invoke(day)
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
        } else if (day.checkListOfEvents(options.listOfEvents, currentDay)){
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
}


