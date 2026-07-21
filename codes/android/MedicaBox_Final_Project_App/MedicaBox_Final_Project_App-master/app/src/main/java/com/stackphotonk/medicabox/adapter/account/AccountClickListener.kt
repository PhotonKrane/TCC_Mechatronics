package com.stackphotonk.medicabox.adapter.account

import com.stackphotonk.medicabox.model.optionsModel

class AccountClickListener (val clickListener: (line : optionsModel) -> Unit) {
    fun onClick (line: optionsModel) = clickListener(line)
}