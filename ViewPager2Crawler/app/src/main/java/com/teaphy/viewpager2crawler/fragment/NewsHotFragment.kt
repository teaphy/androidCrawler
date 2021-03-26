package com.teaphy.viewpager2crawler.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.teaphy.viewpager2crawler.R


private const val ARG_NAME = "name"

class NewsHotFragment : Fragment() {


    private var mName: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logMsg("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mName = it.getString(ARG_NAME)
        }
        logMsg("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logMsg("onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.content_text).text = mName

        logMsg("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        logMsg("onStart")
    }

    override fun onResume() {
        super.onResume()

        logMsg("onResume")
    }

    override fun onPause() {
        super.onPause()
        logMsg("onPause")
    }

    override fun onStop() {
        super.onStop()
        logMsg("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logMsg("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logMsg("onDetach")
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String) =
            NewsHotFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                }
            }
    }

    private fun logMsg(msg: String) {
        Log.e("teaphy", "${javaClass.name} - $msg")
    }
}