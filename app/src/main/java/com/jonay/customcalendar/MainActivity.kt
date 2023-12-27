package com.jonay.customcalendar

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonay.customcalendar.adapter.LogAdapter
import com.jonay.customcalendar.common.utils.viewBinding.viewBinding
import com.jonay.customcalendar.databinding.ActivityMainBinding
import com.jonay.customcalendar.enums.Months
import com.jonay.customcalendar.enums.StartDayOfWeek

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
        val list = listOf(22,25,30)

        val opt = CustomCalendarOptions().apply {
            month = Months.JANUARY
//            year = 2025
        }

        val customCalendar = CustomCalendar(
            context = this,
            options = opt,
            listOfEvents = list
        ).apply {
            onClick = {
                logList.add("Day: $it clicked")
                myAdapter.updateList(logList)
                binding.clickLog.smoothScrollToPosition(logList.size-1)
            }
        }

        supportFragmentManager.beginTransaction().apply {
            replace(binding.calendarContainer.id, customCalendar)
            commit()
        }
    }

    private fun initRecyclerView() = binding.apply {
        clickLog.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = myAdapter
        }
    }
}