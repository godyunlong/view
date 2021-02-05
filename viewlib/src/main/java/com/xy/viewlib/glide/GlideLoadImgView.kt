package com.xy.viewlib.glide

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.xy.viewlib.glide.GlideProgressController
import com.xy.viewlib.glide.OnProgressListener


class GlideLoadImgView(context: Context,attributeSet: AttributeSet?)
    : AppCompatImageView(context,attributeSet),OnProgressListener{
    var progressListener:OnProgressListener?=null
    private val glideProgressController: GlideProgressController = GlideProgressController(this)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        glideProgressController?.onDraw(canvas)
    }

    override fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {
        glideProgressController.onProgress(isComplete,percentage, bytesRead, totalBytes)
        progressListener?.onProgress(isComplete,percentage, bytesRead, totalBytes)
    }
}