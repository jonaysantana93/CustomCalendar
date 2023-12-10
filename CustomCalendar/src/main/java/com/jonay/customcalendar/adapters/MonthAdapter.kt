package com.jonay.customcalendar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.databinding.CustomCalendarMonthYearItemBinding

class MonthAdapter(private val list: List<String>): RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {
    var onMonthClicked: ((pos:Int) -> Unit)? = null

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder =
        MonthViewHolder(
            CustomCalendarMonthYearItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            itemView.setOnClickListener {
                onMonthClicked?.invoke(position)
            }
        }
    }

    class MonthViewHolder(private val binding: CustomCalendarMonthYearItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) = binding.apply {
            binding.monthName.text = name
        }
    }
}