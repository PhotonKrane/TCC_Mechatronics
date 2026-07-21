package com.stackphotonk.medicabox.adapter.pills

import com.stackphotonk.medicabox.model.MedicModel

class PillsClickListener(val clickListener: (remedio : MedicModel) -> Unit) {
    fun onClick (remedio:MedicModel) = clickListener(remedio)
}
