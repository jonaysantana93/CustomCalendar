package com.jonay.customcalendar.common.interfaces

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ViewHolderInitializerInterface {
    fun initialize(parent: ViewGroup): RecyclerView.ViewHolder
}