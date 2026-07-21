package com.stackphotonk.medicabox.adapter.account

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R

class AccountViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {
    val text: TextView = itemView.findViewById(R.id.textAccount)
}