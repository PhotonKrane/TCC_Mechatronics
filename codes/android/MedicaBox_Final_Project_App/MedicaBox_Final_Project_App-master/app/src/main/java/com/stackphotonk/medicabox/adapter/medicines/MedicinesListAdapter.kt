package com.stackphotonk.medicabox.adapter.medicines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.model.BeltModel

class MedicinesListAdapter(val list: ArrayList<BeltModel>, val clickListener: MedicinesClickListener) : RecyclerView.Adapter<MedicinesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicinesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_belt_list, parent, false)

        return MedicinesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MedicinesViewHolder,
        position: Int
    ) {
        val medicine = list[position]

        holder.textNotify.text = medicine.name
        var hour = medicine.TIHour.toString()
        if(hour == "0") {
            hour = "00"
        }
        var minute = medicine.TIMin.toString()
        if(minute == "0") {
            minute = "00"
        }

        holder.TI.text = "${hour}hrs${minute}"

        holder.itemView.setOnClickListener {
            clickListener.onClick(medicine)
        }

    }

    override fun getItemCount(): Int = list.size
}