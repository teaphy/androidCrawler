package com.teaphy.pagersnapdemo.start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.teaphy.pagersnapdemo.CustomAdapter
import com.teaphy.pagersnapdemo.snap.CustomPagerSnapHelper
import com.teaphy.pagersnapdemo.snap.Gravity
import com.teaphy.pagersnapdemo.R

class StartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val listData = listOf<String>("A", "B", "C", "D", "E", "F", "G")

    private val snapHelper = CustomPagerSnapHelper(gravity = Gravity.start)

    private val customAdapter: CustomAdapter by lazy {
        CustomAdapter(listData, limitOffset)
    }

    private val limitOffset: Int by lazy {
        SizeUtils.dp2px(96F)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initView()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recycler_view)

        with(recyclerView) {
            layoutManager =
                LinearLayoutManager(this@StartActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = customAdapter

            val decoration =
                StartSpacesItemDecoration(SizeUtils.dp2px(6F), limitOffSet = limitOffset)
            addItemDecoration(decoration)
            snapHelper.attachToRecyclerView(this)
        }
    }
}