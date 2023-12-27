package com.jonay.customcalendar.adapter.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.common.interfaces.ViewHolderInitializerInterface
import com.jonay.customcalendar.databinding.CustomCalendarMonthYearTextItemBinding
import com.jonay.customcalendar.extensions.getNamesOfMonths
import com.jonay.customcalendar.extensions.getNumberOfMonthWithName
import java.util.Calendar

class MonthAdapter: RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {
    var onClick: ((month: Int) -> Unit)? = null

    private val list = Calendar.getInstance().getNamesOfMonths()

    override fun getItemCount() : Int = Integer.MAX_VALUE
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MonthViewHolder = MonthViewHolder.initialize(parent)
    override fun onBindViewHolder(holder: MonthViewHolder, position : Int) {
        val itemPosition = position % list.size
        holder.apply {
            bindMonth(list[itemPosition])
            itemView.setOnClickListener {
                onClick?.invoke(Calendar.getInstance().getNumberOfMonthWithName(list[itemPosition]))
            }
        }
    }

    class MonthViewHolder(private val binding: CustomCalendarMonthYearTextItemBinding): RecyclerView.ViewHolder(binding.root) {

        companion object: ViewHolderInitializerInterface {
            override fun initialize(parent : ViewGroup) : MonthViewHolder =
                MonthViewHolder(
                    CustomCalendarMonthYearTextItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

        }

        fun bindMonth(month: String){
            binding.title.text = month
        }
    }
}