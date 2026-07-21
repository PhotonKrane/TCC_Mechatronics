package com.stackphotonk.medicabox.adapter.pacients

import com.stackphotonk.medicabox.model.pacientModel

class pacientClickListener (val clickListener: (line : pacientModel) -> Unit) {
    fun onClick (line: pacientModel) = clickListener(line)
}