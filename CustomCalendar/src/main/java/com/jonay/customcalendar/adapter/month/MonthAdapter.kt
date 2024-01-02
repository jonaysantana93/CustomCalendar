package com.jonay.customcalendar.adapter.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.R
import com.jonay.customcalendar.common.interfaces.ViewHolderInitializerInterface
import com.jonay.customcalendar.databinding.CustomCalendarMonthTextItemBinding
import com.jonay.customcalendar.extensions.getNamesOfMonths
import com.jonay.customcalendar.extensions.getNumberOfMonthWithName
import com.jonay.customcalendar.extensions.getResource
import java.util.Calendar

class MonthAdapter(month: String?) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {
    var onClick: ((month: Int, position: Int) -> Unit)? = null

    private val list = Calendar.getInstance().getNamesOfMonths()
    var selectedPosition: Int = list.indexOfFirst { it == month }

    override fun getItemCount() : Int = Integer.MAX_VALUE
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MonthViewHolder = MonthViewHolder.initialize(parent)
    override fun onBindViewHolder(holder: MonthViewHolder, position : Int) {
        val itemPosition = position % list.size
        holder.apply {
            bindMonth(list[itemPosition], position == selectedPosition)
            itemView.setOnClickListener {
                onClick?.invoke(Calendar.getInstance().getNumberOfMonthWithName(list[itemPosition]), position)
            }
        }
    }

    class MonthViewHolder(private val binding: CustomCalendarMonthTextItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object: ViewHolderInitializerInterface {
            override fun initialize(parent : ViewGroup) : MonthViewHolder =
                MonthViewHolder(
                    CustomCalendarMonthTextItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bindMonth(month : String, selected : Boolean) = binding.apply {
            if (selected){
                monthContainer.setCardBackgroundColor(R.color.light_gray.getResource(binding.root.context))
                title.setTextColor(R.color.white.getResource(binding.root.context))
            } else {
                monthContainer.setCardBackgroundColor(R.color.white.getResource(binding.root.context))
                title.setTextColor(R.color.black.getResource(binding.root.context))
            }
            title.text = month
        }
    }
}