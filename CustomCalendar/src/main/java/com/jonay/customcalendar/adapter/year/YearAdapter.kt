package com.jonay.customcalendar.adapter.year

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.common.interfaces.ViewHolderInitializerInterface
import com.jonay.customcalendar.databinding.CustomCalendarYearTextItemBinding

class YearAdapter(private val currentYear: Int): RecyclerView.Adapter<YearAdapter.YearViewHolder>() {
    var onClick: ((year: Int) -> Unit)? = null

    override fun getItemCount() : Int = Int.MAX_VALUE
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : YearViewHolder = YearViewHolder.initialize(parent)
    override fun onBindViewHolder(holder : YearViewHolder, position : Int) {
        holder.apply {
            val year = currentYear+position
            bindYear(year.toString())
            itemView.setOnClickListener {
                onClick?.invoke(year)
            }
        }
    }

    class YearViewHolder(private val binding: CustomCalendarYearTextItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object: ViewHolderInitializerInterface {
            override fun initialize(parent : ViewGroup) : YearViewHolder =
                YearViewHolder(
                    CustomCalendarYearTextItemBinding.inflate(
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