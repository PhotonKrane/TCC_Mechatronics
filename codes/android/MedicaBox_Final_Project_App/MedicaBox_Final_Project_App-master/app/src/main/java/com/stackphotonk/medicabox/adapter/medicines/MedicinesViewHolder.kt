package com.stackphotonk.medicabox.adapter.medicines

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R

class MedicinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textNotify : TextView = itemView.findViewById(R.id.medicText)
    val TI : TextView = itemView.findViewById(R.id.timeInterval)
}