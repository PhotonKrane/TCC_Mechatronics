package com.stackphotonk.medicabox.adapter.belt

import com.stackphotonk.medicabox.model.BeltModel

class BeltClickListener (val clickListener: (line : BeltModel) -> Unit) {
    fun onClick (line: BeltModel) = clickListener(line)
}