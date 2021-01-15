package com.xy.viewlib.picker

import android.view.View

interface PickerListener<T> {
    fun onSelect(view:View,data:T)
}