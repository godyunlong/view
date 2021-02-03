package com.xy.viewlib.dateview

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

class DateViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
    private val views: SparseArray<View?> = SparseArray()
    fun setText(@IdRes viewId: Int, string: String?){
        string?.run {
            val textView = getTextView(viewId)
            textView?.text = this
        }
    }


    fun getTextView(@IdRes viewId: Int): TextView? {
        return getView<TextView>(viewId)
    }

    fun getImageView(@IdRes viewId: Int): ImageView? {
        return getView<ImageView>(viewId)
    }

    fun<VI : View> getView(@IdRes viewId: Int): VI? {
        var view = views[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
        }
        if (view != null) {
            views.put(viewId, view)
            return view as VI
        }else
            return null
    }
}