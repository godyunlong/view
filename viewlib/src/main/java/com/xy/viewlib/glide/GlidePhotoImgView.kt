package com.xy.viewlib.glide

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.github.chrisbanes.photoview.PhotoView

class GlidePhotoImgView(context: Context,attributeSet: AttributeSet?=null)
    : PhotoView(context,attributeSet), OnProgressListener {
    private val glideProgressController: GlideProgressController = GlideProgressController(this)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        glideProgressController?.onDraw(canvas)
    }

    override fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {
        glideProgressController.onProgress(isComplete,percentage, bytesRead, totalBytes)
    }
}