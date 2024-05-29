// WebViewActivity.kt

package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private lateinit var browser: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        browser = binding.webView
        browser.settings.javaScriptEnabled = true
        browser.settings.domStorageEnabled = true
        browser.settings.javaScriptCanOpenWindowsAutomatically = true
        browser.settings.setSupportMultipleWindows(true)
        browser.settings.allowFileAccess = true
        browser.settings.allowContentAccess = true

        browser.addJavascriptInterface(this, "Android")

        browser.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // 페이지 로드 완료 후 추가 작업이 필요하다면 여기에 추가하세요.
            }
        }

        // 주소 정보 입력 페이지를 불러옵니다.
        browser.loadUrl("file:///android_asset/daum.html")
    }

    @JavascriptInterface
    fun processDATA(data: String?) {
        data?.let { address ->
            // 주소를 가져와서 EditText에 설정합니다.
            setResult(RESULT_OK, Intent().apply { putExtra("address", address) })
            finish()
        }
    }
}
