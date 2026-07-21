package com.stackphotonk.medicabox.adapter.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.model.optionsModel

class AccountListAdapter (val listaAccount : ArrayList<optionsModel>, val onCLickListener: AccountClickListener) : RecyclerView.Adapter<AccountViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_account_list, parent, false)

        return AccountViewHolder(view)
    }

    override fun getItemCount(): Int = listaAccount.size

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = listaAccount[position]

        holder.text.text = account.info
        holder.itemView.setOnClickListener {
            onCLickListener.onClick(account)
        }
    }
}