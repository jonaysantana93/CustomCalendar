package com.jonay.customcalendar.adapter.year

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.common.interfaces.ViewHolderInitializerInterface
import com.jonay.customcalendar.databinding.CustomCalendarMonthYearTextItemBinding

class YearAdapter: RecyclerView.Adapter<YearAdapter.YearViewHolder>() {

    override fun getItemCount() : Int = Integer.MAX_VALUE
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : YearViewHolder = YearViewHolder.initialize(parent)
    override fun onBindViewHolder(holder : YearViewHolder, position : Int) {
        holder.bindYear("")
    }


    class YearViewHolder(private val binding: CustomCalendarMonthYearTextItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object: ViewHolderInitializerInterface {
            override fun initialize(parent : ViewGroup) : YearViewHolder =
                YearViewHolder(
                    CustomCalendarMonthYearTextItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bindYear(year: String) = binding.apply {
            title.text = year
        }
    }
}