package com.jonay.customcalendar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.databinding.CustomCalendarMonthYearItemBinding

class YearAdapter(private val list: List<String>): RecyclerView.Adapter<YearAdapter.YearViewHolder>() {
    var onYearClicked: ((pos:Int) -> Unit)? = null

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder =
        YearViewHolder(
            CustomCalendarMonthYearItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            itemView.setOnClickListener {
                onYearClicked?.invoke(position)
            }
        }
    }

    class YearViewHolder(private val binding: CustomCalendarMonthYearItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(name: String) = binding.apply {
            binding.monthName.text = name
        }
    }
}