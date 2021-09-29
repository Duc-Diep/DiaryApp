package com.example.dictionary.events

import android.view.View


abstract class DoubleClickListener : View.OnClickListener {
    private val DOUBLE_CLICK_TIME_DELTA = 200

    var lastClickTime:Long = 0


    override fun onClick(v:View) {
        var clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            onDoubleClick()
            lastClickTime = 0
        } else {
            onSingleClick()
        }
        lastClickTime = clickTime
    }

     abstract fun onSingleClick()
     abstract fun onDoubleClick()
}
