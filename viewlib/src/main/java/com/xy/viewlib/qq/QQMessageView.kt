package com.xy.viewlib.qq

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.xy.viewlib.R
import com.xy.viewlib.dp2px
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class QQMessageView (context: Context,attrs: AttributeSet?= null) : View(context,attrs) {
    var mPaint: Paint? = null
    var mColor = Color.RED
    //固定圆半径
    var mFixCircleRadius = 0f
    //固定圆最大半径
    private var mFixCircleRadiusMax = 7f
    //固定圆最小半径
    private var mFixCircleRadiusMix = 2f
    //拖拽圆半径
    private var mDragCircleRadius = 10f
    //固定圆坐标
    private val mFixPoint = PointF()
    //拖拽圆坐标
    private val mDragPoint = PointF()
    //图片
    var mBitmap: Bitmap? = null
    //动画时长
    private val mDuration = 350
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQMessageView)
        mColor = typedArray.getColor(R.styleable.QQMessageView_circleColor, ContextCompat.getColor(context, R.color.red_e32c))
        mDragCircleRadius = typedArray.getDimension(R.styleable.QQMessageView_dragCircleRadius, dp2px(context,mDragCircleRadius).toFloat())
        mFixCircleRadiusMax = typedArray.getDimension(R.styleable.QQMessageView_fixCircleRadiusMax, dp2px(context,mFixCircleRadiusMax).toFloat())
        mFixCircleRadiusMix = typedArray.getDimension(R.styleable.QQMessageView_fixCircleRadiusMin, dp2px(context,mFixCircleRadiusMix).toFloat())
        typedArray.recycle()
        mFixCircleRadius = mFixCircleRadiusMax
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.isDither = true
        mPaint?.color = mColor
    }

    override fun onDraw(canvas: Canvas) {
        val paint = mPaint ?: return
        canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragCircleRadius,paint)
        val path = createPath()
        if (path != null) {
            //画固定圆
            canvas.drawCircle(mFixPoint.x, mFixPoint.y, mFixCircleRadius, paint)
            //3.画贝塞尔曲线
            canvas.drawPath(path, mPaint!!)
        }
        val bitmap = mBitmap?:return
        if (bitmap.isRecycled)return
        canvas.drawBitmap(bitmap, mDragPoint.x - bitmap.width / 2,
            mDragPoint.y - bitmap.height / 2, mPaint)
    }

    /**
     * 创建贝塞尔曲线
     */
    private fun createPath(): Path? {
        val dx = mDragPoint.x - mFixPoint.x
        val dy = mDragPoint.y - mFixPoint.y
        val distance = sqrt(dx * dx + dy * dy.toDouble())
        mFixCircleRadius = (mFixCircleRadiusMax - distance / 20f).toFloat()
        if (mFixCircleRadius < mFixCircleRadiusMix) return null //半径小于最小值，不用画
        //对比邻
        val tanA = dy / dx
        //获取A的角度
        val atan = Math.atan(tanA.toDouble())
        //p0的x轴位置=固定圆x轴+固定圆半径*sinA
        val p0x =
            (mFixPoint.x + mFixCircleRadius * sin(atan)).toFloat()
        //p0的y轴位置=固定圆y轴-固定圆半径*cosA
        val p0y =
            (mFixPoint.y - mFixCircleRadius * cos(atan)).toFloat()

        //p1的x轴位置=拖拽圆x轴+拖拽圆半径*sinA
        val p1x =
            (mDragPoint.x + mDragCircleRadius * sin(atan)).toFloat()
        //p1的y轴位置=拖拽圆y轴-拖拽圆半径*cosA
        val p1y =
            (mDragPoint.y - mDragCircleRadius * cos(atan)).toFloat()

        //p2的x轴位置=拖拽圆x轴-拖拽圆半径*sinA
        val p2x =
            (mDragPoint.x - mDragCircleRadius * sin(atan)).toFloat()
        //p2的y轴位置=拖拽圆y轴+拖拽圆半径*cosA
        val p2y =
            (mDragPoint.y + mDragCircleRadius * cos(atan)).toFloat()

        //p3的x轴位置=固定圆x轴-固定圆半径*sinA
        val p3x =
            (mFixPoint.x - mFixCircleRadius * sin(atan)).toFloat()
        //p3的y轴位置=固定圆y轴+固定圆半径*cosA
        val p3y =
            (mFixPoint.y + mFixCircleRadius * cos(atan)).toFloat()
        val mBethelPath = Path()
        mBethelPath.moveTo(p0x, p0y)
        //固定点x0,y0，取中心点
        val x0 = (mDragPoint.x + mFixPoint.x) / 2
        val y0 = (mDragPoint.y + mFixPoint.y) / 2
        mBethelPath.quadTo(x0, y0, p1x, p1y)
        mBethelPath.lineTo(p2x, p2y)
        mBethelPath.quadTo(x0, y0, p3x, p3y)
        mBethelPath.close()
        return mBethelPath
    }

    fun initPoint(x: Float, y: Float) {
        mFixPoint.x = x
        mFixPoint.y = y
        mDragPoint.x = x
        mDragPoint.y = y
        invalidate()
    }

    fun updateDragPoint(x: Float, y: Float) {
        mDragPoint.x = x
        mDragPoint.y = y
        invalidate()
    }

    fun setBitmap(bitmap: Bitmap?) {
        mBitmap = bitmap
    }

    /**
     * 手指抬起
     */
    fun actionUp(listener: IActionUpListener) {
        if (mFixCircleRadius < mFixCircleRadiusMix) {
            listener.dismiss(mDragPoint)
        } else {
            springBackAnimation(listener)
        }
    }

    /**
     * 回弹,开启动画回弹
     */
    private fun springBackAnimation(listener: IActionUpListener) {
        val valueAnimator = ObjectAnimator.ofFloat(1f, 0f)
        valueAnimator.duration = mDuration.toLong()
        valueAnimator.interpolator = OvershootInterpolator(3f)
        val endPointF = PointF(mDragPoint.x, mDragPoint.y)
        valueAnimator.addUpdateListener { animation -> //不断更新拖拽点坐标
            val percent = animation.animatedValue as Float
            val endX = mFixPoint.x + (endPointF.x - mFixPoint.x) * percent
            val endY = mFixPoint.y + (endPointF.y - mFixPoint.y) * percent
            updateDragPoint(endX, endY)
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                listener.springBack()
            }
        })
        valueAnimator.start()
    }

}