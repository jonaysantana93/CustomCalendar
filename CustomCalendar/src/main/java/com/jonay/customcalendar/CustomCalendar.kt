package com.jonay.customcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlin.math.log

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
    private val calendar: Calendar by lazy { Calendar.getInstance()
        .apply {
            set(MONTH, options.month.value)
//            set(YEAR, options.year)
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

        repeatOnLifecycleStarted {
            viewModel.nextYearPosition.collect{ xPosition ->
                binding.nextYearText.apply {
                    val params = layoutParams as MarginLayoutParams
                    params.marginStart = xPosition
                    layoutParams = params
                }

                Log.d("JONAY", "nextYearPosition.xPosition -> $xPosition")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun buildMonthsRecyclerView() = binding.apply {
        currentYearText.text = calendar.get(YEAR).toString()
        nextYearText.text = calendar.get(YEAR).plus(1).toString()

        calendar.getDisplayName(MONTH, SHORT, Locale.getDefault())?.let { currentMonth ->
            customMonthAdapter = MonthAdapter(currentMonth).apply {
                onClick = { month, position ->
                    options.month = Months.getCustomMonth(month)
                    calendar.set(MONTH, options.month.value)
                    selectedPosition = position
                    updateEvents?.invoke(month, (calendar.get(YEAR) + position))
                }
            }

            monthsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = customMonthAdapter
                setHasFixedSize(true)
                addOnScrollListener(MonthsOnScrollListener())
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

            if (day.checkIfIsaPassDay(
                    this,
                    calendar.customClone(),
                    currentDate,
                    options.passDayLockTextColor
                )
            ) {
                day.checkIfIsCurrentDay(this, calendar.customClone(), currentDate, options)
                    ?.let { daySelectedView = it }
                day.checkIfHaveEvent(this)
                root.onclickListener(day, this)
            }
        }.root

    private fun Int.checkIfHaveEvent(binding: CustomCalendarTextItemBinding) {
        if (this.checkListOfEvents(options.listOfEvents, currentDay)) {
            binding.apply {
                dayContainer.setCardBackgroundColor(
                    options.dayCellBackgroundWithEvent.getResource(
                        context
                    )
                )
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
        if (day == currentDay) {
            dayContainer.background = ContextCompat.getDrawable(context, R.drawable.cardview_border)
            dayText.setTextColor(R.color.black.getResource(context))
            daySelectedView = this
        } else if (day.checkListOfEvents(options.listOfEvents, currentDay)) {
            dayContainer.setCardBackgroundColor(
                options.dayCellBackgroundWithEvent.getResource(
                    context
                )
            )
            dayText.setTextColor(options.dayTextColorWithEvent.getResource(context))
        } else {
            dayContainer.setCardBackgroundColor(R.color.white.getResource(context))
            dayText.setTextColor(R.color.black.getResource(context))
        }
    }

    private fun CustomCalendarTextItemBinding.updateSelectedDay(day: Int) {
        if (day != currentDay) {
            daySelectedView = this.apply {
                dayContainer.setCardBackgroundColor(R.color.light_gray.getResource(context))
                dayText.setTextColor(R.color.white.getResource(context))
            }
        }
        daySelected = day
    }

    inner class MonthsOnScrollListener : RecyclerView.OnScrollListener() {
        private val cellWidthPixels = context.resources.getDimensionPixelSize(R.dimen.cell_month_width)
        private val totalWidthOfCellPixelsOneYear = cellWidthPixels.times(12)
        private val screenWidthPixels = context.getScreenWidthResolution()

        private val startMonth = (calendar.get(MONTH)) //0..11 - Jan..Dec
        private val startYear = calendar.get(YEAR) // 2024
        private val totalWidthOfCellPixelsOneYearUsingStartMonth = cellWidthPixels.times(12-startMonth)


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val scrollX = recyclerView.computeHorizontalScrollOffset()

            if (scrollX > totalWidthOfCellPixelsOneYearUsingStartMonth-screenWidthPixels) { // Año != Año actual
                val nextPosition = (screenWidthPixels-cellWidthPixels)-(scrollX-screenWidthPixels)
                viewModel.setNextYearPosition(nextPosition)



//                if (screenWidthPixels-xPositionEndScreen < 0) {
//                    val nextPosition = screenWidthPixels - (scrollX-(screenWidthPixels/2))
//                    viewModel.setNextYearPosition(nextPosition)
//                }
            } else { /*Es el año en curso */
                viewModel.setNextYearPosition(screenWidthPixels)
            }
        }

        private fun Context.getScreenHeightResolution(): Int =
            DisplayMetrics().apply {
                setSystemDisplayWithMetric(this@getScreenHeightResolution, this)
            }.heightPixels

        private fun Context.getScreenWidthResolution(): Int =
            DisplayMetrics().apply {
                setSystemDisplayWithMetric(this@getScreenWidthResolution, this)
            }.widthPixels

        @Suppress("DEPRECATION")
        private fun setSystemDisplayWithMetric(context: Context, metrics: DisplayMetrics) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.apply {
                    getMetrics(metrics)
                }
            } else {
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay?.apply {
                    getMetrics(metrics)
                }
            }
    }
}