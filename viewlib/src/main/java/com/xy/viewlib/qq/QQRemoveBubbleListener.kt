package com.xy.viewlib.qq

import android.view.View

interface QQRemoveBubbleListener<T> {
    fun onQQRemoveBubble(view: View, item:T?, type:Int)
}