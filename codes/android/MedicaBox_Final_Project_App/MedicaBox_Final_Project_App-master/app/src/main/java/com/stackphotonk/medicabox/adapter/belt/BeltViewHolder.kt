package com.stackphotonk.medicabox.adapter.belt

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R

class BeltViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {
    val text : TextView = itemView.findViewById(R.id.medicText)
    val TI : TextView = itemView.findViewById(R.id.timeInterval)
}