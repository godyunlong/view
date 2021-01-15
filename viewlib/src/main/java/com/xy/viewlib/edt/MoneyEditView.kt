package com.xy.viewlib.edt

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.xy.viewlib.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class MoneyEditView (context: Context,attrs: AttributeSet):AppCompatEditText(context,attrs) , InputFilter {
    private var decimalLength:Int = 2
    private var p: Pattern
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MoneyEditView)
        decimalLength = array.getInt(R.styleable.MoneyEditView_decimal_length,2)
        p = Pattern.compile("([0-9]|\\.)*")
    }
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        if (source == null)return ""
        val oldest = dest.toString()
        if ("" == source.toString()) {
            return ""
        }
        val m: Matcher = p.matcher(source)
        if (oldest.contains(".")) {
            if (!m.matches()) {//已经存在小数点的情况下，只能输入数字
                return ""
            }
        } else {
            if (!m.matches()) {//已经存在小数点的情况下，只能输入数字
                return ""
            } else {
                if ("0" == source && "0" == oldest) {
                    return ""
                }
            }
            if ("." == source && TextUtils.isEmpty(oldest)) {
                return ""
            }
        }

        //验证小数位精度是否正确

        //验证小数位精度是否正确
        if (oldest.contains(".")) {
            val index = oldest.indexOf(".")
            val len = dend - index
            //小数位只能2位
            if (len > decimalLength) {
                return dest!!.subSequence(dstart, dend)
            }
        }
        return "${dest?.subSequence(dstart, dend)}$source.toString()"
    }
}