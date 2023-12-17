package com.jonay.customcalendar.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface AdapterInitializeInterface {
    fun initialize(parent: ViewGroup): RecyclerView.ViewHolder
}