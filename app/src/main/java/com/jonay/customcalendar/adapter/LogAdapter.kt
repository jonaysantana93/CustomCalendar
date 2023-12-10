package com.jonay.customcalendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonay.customcalendar.databinding.LogItemBinding

class LogAdapter(private var list: List<String>): RecyclerView.Adapter<LogItem>() {

    override fun getItemCount(): Int = list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItem  = LogItem.initialize(parent)
    override fun onBindViewHolder(holder: LogItem, position: Int) {
        holder.bindLogItem(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<String>) {
        list = newList
        notifyDataSetChanged()
    }
}

class LogItem(private val binding: LogItemBinding): RecyclerView.ViewHolder(binding.root){
    companion object: InitializeItemInterface {
        override fun initialize(parent: ViewGroup): LogItem =
            LogItem(
                LogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
    }

    fun bindLogItem(txt: String) = binding.apply {
        logTxt.text = txt
    }
}