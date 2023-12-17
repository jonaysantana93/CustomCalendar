package com.jonay.customcalendar.adapter.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.adapter.AdapterInitializeInterface
import com.jonay.customcalendar.databinding.CustomCalendarMonthItemBinding

class MonthAdapter(private val list: List<String>): RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder = MonthViewHolder.initialize(parent)
    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.apply {
            bindMonth(list[position])
        }
    }

    class MonthViewHolder(private val binding: CustomCalendarMonthItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object: AdapterInitializeInterface {
            override fun initialize(parent: ViewGroup): MonthViewHolder =
                MonthViewHolder(
                    CustomCalendarMonthItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bindMonth(item: String) = binding.apply {
            monthText.text = item
        }
    }
}