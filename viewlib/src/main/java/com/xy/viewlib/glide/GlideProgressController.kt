package com.xy.viewlib.glide

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.xy.viewlib.R
import com.xy.viewlib.dp2px
import com.xy.viewlib.getColor
import kotlin.math.min

class GlideProgressController(private val view:View)  {
    private var progress :Int = 0
    private var isComplete = false
    private var minSize = 30F

    fun onDraw(canvas: Canvas?){
        if (isComplete || progress <=0 || progress>100)return
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
        val paint = Paint()
        paint.color = getColor(view.context, R.color.gray_9999)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        val textSize = min(min(view.width,view.height) /2, dp2px(view.context,minSize))/3
        paint.textSize = textSize.toFloat()

        val fontMetrics: Paint.FontMetrics = paint.fontMetrics
        val top = fontMetrics.top //为基线到字体上边框的距离,即上图中的top
        val bottom = fontMetrics.bottom //为基线到字体下边框的距离,即上图中的bottom
        val baseLineY = (view.height/2f - top / 2 - bottom / 2) //基线中间点的y轴计算公式

        canvas?.drawText("$progress%",view.width/2f,baseLineY,paint)
    }

    fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {
        this.isComplete = isComplete
        this.progress = percentage
        view.invalidate()
    }
}