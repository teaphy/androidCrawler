package com.teaphy.pagersnapdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.*
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import com.teaphy.pagersnapdemo.center.CenterActivity
import com.teaphy.pagersnapdemo.end.EndActivity
import com.teaphy.pagersnapdemo.start.StartActivity
import com.teaphy.pagersnapdemo.start.StartSpacesItemDecoration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.init(application)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
        }

        findViewById<Button>(R.id.center_button).setOnClickListener {
            startActivity(Intent(this, CenterActivity::class.java))
        }

        findViewById<Button>(R.id.end_button).setOnClickListener {
            startActivity(Intent(this, EndActivity::class.java))
        }
    }
}