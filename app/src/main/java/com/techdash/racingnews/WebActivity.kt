package com.techdash.racingnews

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        if (intent != null) {
            val url = intent.getStringExtra("url")
            if (url != null) {
                webView.loadUrl(url)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }
}