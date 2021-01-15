package com.xy.viewlib.glide

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.xy.viewlib.R
import com.xy.viewlib.dp2px
import com.xy.viewlib.getColor
import kotlin.math.min

class GlideProgressController(private val view:View)  {
    private var progress :Float = 0F
    private var isComplete = false
    private var minSize = 30F
    
    fun onDraw(canvas: Canvas?){
        if (isComplete && progress >0)return
        drawOutCircle(canvas)
        drawProgress(canvas)
    }


    private fun drawOutCircle(canvas: Canvas?){
        val paint = Paint()
        paint.color = getColor(view.context, R.color.gray_9999)
        paint.strokeWidth = dp2px(view.context,2F).toFloat()
        paint.style = Paint.Style.STROKE
        val minSize = min(min(view.width,view.height) /2, dp2px(view.context,minSize))
        canvas?.drawCircle(view.width/2F,view.height/2F,minSize/2F,paint)
    }

    private fun drawProgress(canvas: Canvas?){
        var minSize = min(min(view.width,view.height) /2, dp2px(view.context,minSize))
        minSize -= dp2px(view.context, 8F)
        val left = view.width/2f-minSize/2F
        val right = view.width/2f+minSize/2F
        val top = view.height/2f-minSize/2F
        val bottom = view.height/2F+minSize/2F
        val oval = RectF()
        oval.set(left,top,right,bottom)

        val paint = Paint()
        paint.color = getColor(view.context, R.color.gray_9999)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas?.drawArc(oval,-90F,progress*360-90,true,paint)
    }

    fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {
        this.isComplete = isComplete
        this.progress = percentage/100F
        view.invalidate()
    }
    
}