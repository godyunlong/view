package com.xy.viewlib.multi

import android.view.View

interface MultiListener<T> {
    fun onMultiChoseView(view:View,item:T,position:Int,select:Boolean,repeat :Boolean)
}