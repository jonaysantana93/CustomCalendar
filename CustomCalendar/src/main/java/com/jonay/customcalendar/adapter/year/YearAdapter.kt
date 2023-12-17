package com.jonay.customcalendar.adapter.year

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.R
import com.jonay.customcalendar.adapter.AdapterInitializeInterface
import com.jonay.customcalendar.databinding.CustomCalendarYearItemBinding
import java.util.Calendar

class YearAdapter(private val list: List<String>, private val currentYear: String): RecyclerView.Adapter<YearAdapter.YearViewHolder>() {

    private var anchoElementosB: Int = 0

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder = YearViewHolder.initialize(parent)
    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        holder.apply {
            itemView.layoutParams = holder.itemView.layoutParams.apply {
                width = if (list[position] == currentYear) {
                    val nMonths = Calendar.DECEMBER - Calendar.getInstance().get(Calendar.MONTH)+1
                    holder.itemView.context.resources.getDimensionPixelSize(R.dimen.yearCellWidth) * nMonths
                } else {
                    holder.itemView.context.resources.getDimensionPixelSize(R.dimen.yearCellWidth) * 12
                }
            }
            bindYear(list[position])
        }
    }


    fun setAnchoElementosB(ancho: Int) {
        anchoElementosB = ancho
    }

    class YearViewHolder(private val binding: CustomCalendarYearItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object: AdapterInitializeInterface {
            override fun initialize(parent: ViewGroup): YearViewHolder =
                YearViewHolder(
                    CustomCalendarYearItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bindYear(item: String) = binding.apply {
            yearText.text = item
        }
    }
}