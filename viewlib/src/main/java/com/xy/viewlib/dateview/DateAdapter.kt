package com.xy.baselib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.xy.viewlib.R
import com.xy.viewlib.dateview.DateClickListener
import com.xy.viewlib.dateview.DateMode
import com.xy.viewlib.dp2px

class DateAdapter(private val selectBackground:Int,private val selectBackgroundNot:Int,private val toDayBackGround:Int
                  ,private val selectTextColor:Int,private val selectTextColorNot:Int,private val toDayTextColor:Int
                  ,private val commonTextColor:Int,private val listener: DateClickListener)
    :RecyclerView.Adapter<DateViewHolder>() {
    val data = ArrayList<DateMode>()
    var selectPosition = -1
    override fun getItemCount(): Int = data.size
    fun changeSelectPosition(position: Int){
        val oldSelectPosition = selectPosition
        this.selectPosition = position
        notifyItemChanged(position)
        notifyItemChanged(oldSelectPosition)
    }

    fun notifyChangeItem(index:Int,date: DateMode){
        if (index in 0 until this.data.size){
            this.data[index].year = date.year
            this.data[index].month = date.month
            this.data[index].day = date.day
            this.data[index].isToDay = date.isToDay
            this.data[index].nowMoth = date.nowMoth
            notifyItemChanged(index)
        }
    }

    fun setNewData(data:ArrayList<DateMode>){
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }


    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val itemWidth = dp2px(holder.itemView.context,40F)
        holder.getView<View>(R.id.date_title)?.layoutParams = LinearLayout.LayoutParams(itemWidth,itemWidth)
        val text = holder.getTextView(R.id.date_title)
        if (position in 0 until data.size) {
            val item = data[position]
            text?.text = "${item.day}"
            if (selectPosition == position) {
                text?.setBackgroundResource(selectBackground)
                text?.setTextColor(selectTextColor)
            } else if (item.isToDay) {
                text?.setBackgroundResource(toDayBackGround)
                text?.setTextColor(toDayTextColor)
            } else if (!item.nowMoth) {
                text?.setTextColor(selectTextColorNot)
                text?.setBackgroundResource(selectBackgroundNot)
            } else {
                text?.setTextColor(commonTextColor)
                text?.setBackgroundResource(selectBackgroundNot)
            }
            holder.itemView.setOnClickListener { listener.onDateClickListener(item,position) }
        }
    }
}