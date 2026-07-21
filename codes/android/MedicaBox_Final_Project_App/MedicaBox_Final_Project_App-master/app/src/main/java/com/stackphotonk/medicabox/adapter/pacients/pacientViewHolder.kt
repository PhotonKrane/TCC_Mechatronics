package com.stackphotonk.medicabox.adapter.pacients

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R

class pacientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text: TextView = itemView.findViewById(R.id.textPacientEmail)
}