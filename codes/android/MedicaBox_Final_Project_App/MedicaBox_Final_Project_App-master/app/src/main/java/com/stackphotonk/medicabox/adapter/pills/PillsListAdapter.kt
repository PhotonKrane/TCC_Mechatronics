package com.stackphotonk.medicabox.adapter.pills

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.model.MedicModel

class PillsListAdapter (val listaMedic : ArrayList<MedicModel>, val onCLickListener: PillsClickListener) : RecyclerView.Adapter<PillsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rowmediclist, parent, false)

        return PillsViewHolder(view)
    }

    override fun getItemCount(): Int = listaMedic.size

    override fun onBindViewHolder(holder: PillsViewHolder, position: Int) {
        val med = listaMedic[position]
        holder.text.text = med.name
        if (med.image != "") {
            Glide.with(holder.itemView.context).load(med.image).into(holder.image)
        } else{
            holder.image.setImageResource(R.drawable.pill)
        }
        holder.itemView.setOnClickListener {
            onCLickListener.onClick(med)
        }
    }
}