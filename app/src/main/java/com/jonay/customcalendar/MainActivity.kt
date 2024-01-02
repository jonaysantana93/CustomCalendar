package com.jonay.customcalendar

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonay.customcalendar.adapter.LogAdapter
import com.jonay.customcalendar.common.utils.viewBinding.viewBinding
import com.jonay.customcalendar.databinding.ActivityMainBinding
import com.jonay.customcalendar.enums.Months
import com.jonay.customcalendar.enums.StartDayOfWeek
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val logList = mutableListOf<String>()
    private val myAdapter by lazy { LogAdapter(logList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initCustomCalendar()
        initRecyclerView()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initCustomCalendar() {
        val opt = CustomCalendarOptions().apply {
            listOfEvents = listOf(22,25,30)
//            month = Months.AUGUST
//            year = 2025
        }

        Calendar(
            appActivity = this,
            container = binding.calendarContainer.id,
            options = opt
        ).apply {
            onDayClick = {
                logList.add("Day: $it clicked")
                myAdapter.updateList(logList)
                binding.clickLog.smoothScrollToPosition(logList.size-1)
            }

            updateListEvents = { month, year ->
                //We need launch the query to get the new list of events
                updateListOfEvents(emptyList())
            }
        }
    }

    private fun initRecyclerView() = binding.apply {
        clickLog.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = myAdapter
        }
    }
}