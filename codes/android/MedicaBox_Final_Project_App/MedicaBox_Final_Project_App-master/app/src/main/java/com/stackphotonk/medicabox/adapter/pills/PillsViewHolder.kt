package com.stackphotonk.medicabox.adapter.pills

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R

class PillsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image : ImageView = itemView.findViewById(R.id.image)
    val text: TextView = itemView.findViewById(R.id.textName)
}
