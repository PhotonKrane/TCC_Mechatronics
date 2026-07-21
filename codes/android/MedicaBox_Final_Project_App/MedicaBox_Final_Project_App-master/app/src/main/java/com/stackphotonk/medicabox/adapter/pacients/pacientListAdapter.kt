package com.stackphotonk.medicabox.adapter.pacients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.model.pacientModel

class pacientListAdapter (val listPacient : ArrayList<pacientModel>, val onClickListener: pacientClickListener) : RecyclerView.Adapter<pacientViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): pacientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_pacient_list, parent, false)

        return pacientViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: pacientViewHolder,
        position: Int
    ) {
        val pacient = listPacient[position]

        holder.text.text = pacient.email
        holder.itemView.setOnClickListener {
            onClickListener.onClick(pacient)
        }
    }

    override fun getItemCount(): Int = listPacient.size
}