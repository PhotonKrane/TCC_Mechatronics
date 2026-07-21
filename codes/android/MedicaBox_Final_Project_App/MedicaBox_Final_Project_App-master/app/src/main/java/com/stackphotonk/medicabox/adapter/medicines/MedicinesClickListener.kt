package com.stackphotonk.medicabox.adapter.medicines

import com.stackphotonk.medicabox.model.BeltModel

class MedicinesClickListener(val clickListener: (line : BeltModel) -> Unit) {
    fun onClick (line: BeltModel) = clickListener(line)
}