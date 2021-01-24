package com.teaphy.pagersnapdemo.center

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.teaphy.pagersnapdemo.CustomAdapter
import com.teaphy.pagersnapdemo.snap.CustomLinearSnapHelper
import com.teaphy.pagersnapdemo.snap.Gravity
import com.teaphy.pagersnapdemo.R

class CenterActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    private val listData = listOf<String>("A", "B", "C", "D", "E", "F", "G")

    private val snapHelper = CustomLinearSnapHelper(gravity = Gravity.center)

    private val customAdapter: CustomAdapter by lazy {
        CustomAdapter(listData, limitOffset)
    }

    private val limitOffset: Int by lazy {
        SizeUtils.dp2px(96F)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_center)

        initView()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recycler_view)

        with(recyclerView) {
            layoutManager =
                LinearLayoutManager(this@CenterActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = customAdapter

            val decoration =
                CenterSpacesItemDecoration(SizeUtils.dp2px(6F), limitOffSet = limitOffset)
            addItemDecoration(decoration)
            snapHelper.attachToRecyclerView(this)
        }
    }
}