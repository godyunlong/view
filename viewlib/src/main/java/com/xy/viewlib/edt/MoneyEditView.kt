package com.xy.viewlib.edt

import android.content.Context
import android.text.*
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.xy.viewlib.R
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern


class MoneyEditView (context: Context,attrs: AttributeSet):AppCompatEditText(context,attrs) , TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}

    init {
        this.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
        addTextChangedListener(this)
    }

    fun getMoney():Double{
        val str = text.toString()
        if (TextUtils.isEmpty(str))return 0.0
        try {
            return str.toDouble()
        }catch (e: Exception){}
        return 0.0
    }

    override fun afterTextChanged(editable: Editable) {
        val temp = editable.toString()
        if (temp == ".") {
            editable.delete(0, editable.length)
            editable.append("0.")
            return
        }else if (temp.endsWith("..")) {
            editable.delete(editable.length - 1, editable.length)
            return
        }else if (temp.startsWith("00")){
            editable.delete(0, 1)
        }else if (temp.startsWith("0") && !temp.startsWith("0.") && temp.length >=2){
            editable.delete(0, 1)
        }
        val posDot = temp.indexOf(".")
        if (posDot <= 0) return
        if (temp.length - posDot - 1 > 2) {
            editable.delete(posDot + 3, posDot + 4)
            return
        }
    }
}