package com.xy.viewlib.qq

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class MessageRecyclerView(context: Context, attributeSet: AttributeSet?=null)
    :RecyclerView(context,attributeSet) ,MessageTouchListener{
    private var isDown = false
    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return !isDown && super.onInterceptTouchEvent(e)
    }

    override fun onTouchStatus(isDown:Boolean) {
        this.isDown = isDown
    }
}