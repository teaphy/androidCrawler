package com.teaphy.pagersnapdemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils

class CustomAdapter(private val listData: List<String>, private val limitOffset: Int) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = listData[position]

        Log.e("teaphy", "position $position")

        val bgColor = when (position % 3) {
            0 -> ActivityCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark)
            1 -> ActivityCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark)
            2 -> ActivityCompat.getColor(holder.itemView.context, android.R.color.holo_blue_dark)
            else -> ActivityCompat.getColor(holder.itemView.context, android.R.color.holo_blue_dark)
        }
        holder.textView.setBackgroundColor(bgColor)

        val params = holder.itemView.layoutParams as ViewGroup.LayoutParams
        val width = ScreenUtils.getScreenWidth()

        params.width = width - limitOffset


        holder.itemView.layoutParams = params
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)

    }

}