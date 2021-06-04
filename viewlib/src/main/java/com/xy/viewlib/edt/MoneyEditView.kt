package com.xy.viewlib.edt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.xy.viewlib.R
import com.xy.viewlib.dp2px
import com.xy.viewlib.getColor

open class MoneyEditView(context: Context, attrs: AttributeSet?)
    : AppCompatEditText(context, attrs), TextWatcher , OnGlobalLayoutListener{
    private var mTextSize = 0
    private var mTextColor = 0x000000
    private var leadText = "¥"
    init {
        inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
        addTextChangedListener(this)
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.MoneyEditView)
            mTextSize = array.getDimensionPixelSize(R.styleable.MoneyEditView_money_label_size, dp2px(context,15F))
            mTextColor = array.getColor(R.styleable.MoneyEditView_money_label_color, getColor(context,R.color.black))
            leadText = array.getString(R.styleable.MoneyEditView_money_label_text)?:"¥";
            val leadWidth = measureText(mTextSize.toFloat(), leadText)
            setPadding((paddingLeft + leadWidth / 2).toInt(), paddingTop, (paddingRight + leadWidth / 2).toInt(), paddingBottom)
        }
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
        val temp = editable.toString()
        if (temp == ".") {
            editable.delete(0, editable.length)
            editable.append("0.")
        } else if (temp.endsWith("..")) {
            editable.delete(editable.length - 1, editable.length)
        } else if (temp.startsWith("00")) {
            editable.delete(0, 1)
        } else if (temp.startsWith("0") && !temp.startsWith("0.") && temp.length >= 2) {
            editable.delete(0, 1)
        }
        val posDot = temp.indexOf(".")
        if (posDot > 0 && temp.length - posDot - 1 > 2) {
            editable.delete(posDot + 3, posDot + 4)
        }
        resetLebel()
    }

    private fun resetLebel() {
        if (TextUtils.isEmpty(leadText)) return
        val bitmap = fromText()
        val drawableWidth = bitmap.width
        val drawableHeight = bitmap.height
        val content = text.toString()
        val hint = hint.toString()
        val textWidth = measureText(textSize, if (TextUtils.isEmpty(content)) hint else content)
        var left = 0
        when (gravity) {
            Gravity.CENTER -> left = (width / 2 - textWidth / 2 - drawableWidth).toInt()
            Gravity.LEFT -> left = 0
            Gravity.RIGHT -> left = (width - textWidth / 2 - drawableWidth).toInt()
        }
        if (left <= 0) left = 0
        val drawable: Drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(left, 0, left + drawableWidth, drawableHeight)
        setCompoundDrawables(drawable, null, null, null)
    }

    private fun fromText(): Bitmap {
        val paint = Paint()
        paint.textSize = mTextSize.toFloat()
        paint.textAlign = Paint.Align.LEFT
        paint.color = mTextColor
        val fm = paint.fontMetricsInt
        val width = paint.measureText(leadText).toInt()
        val height = fm.descent - fm.ascent
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(leadText, 0f, fm.leading - fm.ascent.toFloat(), paint)
        canvas.save()
        return bitmap
    }

    private fun measureText(textSize: Float, text: String): Float {
        val paint = Paint()
        paint.textSize = textSize //自定义字大小
        paint.textAlign = Paint.Align.CENTER
        return paint.measureText(text)
    }

    override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        resetLebel()
    }
}