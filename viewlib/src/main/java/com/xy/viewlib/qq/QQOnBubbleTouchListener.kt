package com.xy.viewlib.qq

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.AnimationDrawable
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.xy.viewlib.R

/**
 *  点下，原来的View要不可见，WindowManager添加一样的View，可以拖拽
 *  可拖拽的View是原来的View
 *  手指松开，1.消失不见开启消失动画 2.回到原来位置，原来View可见
 */

class QQOnBubbleTouchListener<T>(private val mView: View, private val mContext: Context
) : OnTouchListener , IActionUpListener {

    private var bubbleListener:QQRemoveBubbleListener<T>?=null
        set(value) {bubbleListener = value}
    private var removeType:Int = 0
        set(value) {removeType = value}
    private var touchListener :MessageTouchListener?=null
        set(value) {touchListener = value}
    private var item:T?=null
        set(value) {item = value}
    private val mWindowManger: WindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val qqMessageView: QQMessageView = QQMessageView(mContext)
    private val mBombFrameLayout: FrameLayout = FrameLayout(mContext)
    private val mBombImageView: ImageView = ImageView(mContext)
    private val mLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
    private var mOriginalViewParams: ViewGroup.LayoutParams? = null

    init {
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
        mLayoutParams.format = PixelFormat.TRANSPARENT
        mLayoutParams.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        mBombImageView.setImageResource(R.drawable.anim_bubble_pop)
        mBombFrameLayout.addView(mBombImageView, layoutParams)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mOriginalViewParams = mView.layoutParams
                val bitmap = getBitmap(mView)
                qqMessageView.setBitmap(bitmap)
                mWindowManger.addView(qqMessageView, mLayoutParams)
                val location = IntArray(2)
                mView.getLocationOnScreen(location)
                qqMessageView.initPoint(location[0] + mView.width / 2F, location[1] + mView.height / 2F)
                mView.visibility = View.INVISIBLE
                touchListener?.onTouchStatus(true)
            }
            MotionEvent.ACTION_MOVE -> {
                qqMessageView.updateDragPoint(event.rawX, event.rawY)
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                qqMessageView.actionUp(this)
                touchListener?.onTouchStatus(false)
            }
        }
        return true
    }
    /**
     * 获取bitmap
     */
    private  fun getBitmap(view: View?): Bitmap? {
        if (view == null)return null
        view.buildDrawingCache()
        return view.drawingCache
    }

    override fun springBack() {
        mWindowManger.removeView(qqMessageView)
        mView.layoutParams = mOriginalViewParams
        mView.visibility = View.VISIBLE
    }

    override fun dismiss(pointF: PointF?) {
        mWindowManger.removeView(qqMessageView)
        mBombImageView.x = (pointF?.x?:0F) - mBombImageView.width / 2
        mBombImageView.y = (pointF?.y?:0F) - mBombImageView.height / 2
        mWindowManger.addView(mBombFrameLayout, mLayoutParams)
        //开启爆炸动画
        startBombAnimator()
    }

    /**
     * 开启爆炸动画
     */
    private fun startBombAnimator() {
        val animationDrawable = mBombImageView.drawable
        bubbleListener?.onQQRemoveBubble(item,removeType)
        if (animationDrawable is AnimationDrawable){
            //获取每帧时间,为了延迟移除WindowManager上的爆炸界面
            var time: Long = 0
            for (i in 0 until animationDrawable.numberOfFrames) {
                time += animationDrawable.getDuration(i).toLong()
            }
            animationDrawable.start()
            mBombImageView.postDelayed({ //移除WindowManager上的爆炸界面
                mWindowManger.removeView(mBombFrameLayout)
                mView.visibility = View.GONE
            }, time)
        }
    }
}