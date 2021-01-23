package com.xy.viewlib.qq

import android.graphics.PointF

interface IActionUpListener {
    //回弹
    fun springBack()

    //爆炸动画
    fun dismiss(pointF: PointF?)
}