package com.stackphotonk.medicabox.adapter.belt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.model.BeltModel

class BeltListAdapter (val listBelt : ArrayList<BeltModel>, val onCLickListener: BeltClickListener) : RecyclerView.Adapter<BeltViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeltViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_belt_list, parent, false)

        return BeltViewHolder(view)
    }

    override fun getItemCount(): Int = listBelt.size

    override fun onBindViewHolder(holder: BeltViewHolder, position: Int) {
        val belt = listBelt[position]

        holder.text.text = belt.name
        var hour = belt.TIHour.toString()
        if(hour == "0") {
            hour = "00"
        }
        var minute = belt.TIMin.toString()
        if(minute == "0") {
            minute = "00"
        }

        holder.TI.text = "${hour}hrs${minute}"

        holder.itemView.setOnClickListener {
            onCLickListener.onClick(belt)
        }
    }
}