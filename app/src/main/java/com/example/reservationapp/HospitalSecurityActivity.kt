// HospitalSecurityActivity.kt

package com.example.reservationapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.databinding.ActivityHospitalSecurityBinding

class HospitalSecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalSecurityBinding
    private lateinit var webViewDialog: Dialog
    private val SEARCH_ADDRESS_ACTIVITY = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            showWebViewDialog()
        }

        binding.securityButton.setOnClickListener {
            onBackToHospitalClicked()
        }
    }

    private fun showWebViewDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("우편번호 검색")
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.domStorageEnabled = true // JavaScript가 로컬 저장소에 접근할 수 있도록 설정
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun execDaumPostcode() {
                runOnUiThread {
                    val javascript = "sample6_execDaumPostcode();"
                    webView.evaluateJavascript(javascript, null)
                }
            }
        }, "Android") // JavaScript에서 Android 객체로 접근하기 위한 인터페이스 설정
        webView.loadUrl("https://appassets.androidplatform.net/assets/daum.html")
        builder.setView(webView)
        builder.setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
        webViewDialog = builder.create()
        webViewDialog.show()
    }

    private fun onBackToHospitalClicked() {
        val intent = Intent(this, HospitalMainActivity::class.java)
        intent.putExtra("show_fragment", "hospital")
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        webViewDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_ADDRESS_ACTIVITY && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra("address")?.let { address ->
                binding.etAddress.setText(address)
            }
        }
    }
}
