package com.pearl.damasfollows

import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient

open class MyWebViewClient(activity: Activity):WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        view.loadUrl(url!!)
        return true
    }


}