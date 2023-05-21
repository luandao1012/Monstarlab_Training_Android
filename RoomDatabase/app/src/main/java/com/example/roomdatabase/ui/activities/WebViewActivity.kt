package com.example.roomdatabase.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.example.roomdatabase.R
import com.example.roomdatabase.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    companion object {
        private const val URL = "https://monstar-lab.com/global/"
    }

    private val binding by lazy { ActivityWebViewBinding.inflate(layoutInflater) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(URL)
    }
}