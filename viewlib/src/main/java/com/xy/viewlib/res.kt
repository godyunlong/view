package com.xy.viewlib

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * 获取颜色
 */
fun getColor(context: Context, @ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(context, colorRes)
}

fun dp2px(sContext: Context?, dpValue: Float): Int {
    if (sContext == null) return 0
    val scale = sContext.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}