package com.teaphy.viewpager2crawler.basic

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teaphy.viewpager2crawler.R

/**
 *
 * Create by: teaphy
 * Date: 3/23/21
 * Time: 4:39 PM
 */
class BasicUseAdapter(private val listData: List<String>) :
    RecyclerView.Adapter<BasicUseAdapter.BasicUseViewHolder>() {

    private val listColor = listOf<String>("#CCFF99", "#41F1E5", "#8D41F1", "#FF99CC")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicUseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_basic_use, parent, false)
        return BasicUseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasicUseViewHolder, position: Int) {
        holder.contentText.text = listData[position]

        holder.itemView.setBackgroundColor(Color.parseColor(listColor[position % 4]))
    }

    class BasicUseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentText = itemView.findViewById<TextView>(R.id.content_text)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}