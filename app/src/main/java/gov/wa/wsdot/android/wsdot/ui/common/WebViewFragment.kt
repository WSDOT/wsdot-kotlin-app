package gov.wa.wsdot.android.wsdot.ui.common

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.WebViewFragmentBinding
import gov.wa.wsdot.android.wsdot.util.autoCleared


class WebViewFragment: Fragment() {

    var binding by autoCleared<WebViewFragmentBinding>()

    val args: WebViewFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding = DataBindingUtil.inflate<WebViewFragmentBinding>(
            inflater,
            R.layout.web_view_fragment,
            container,
            false
        )
        binding = dataBinding

        val webView = dataBinding.root.findViewById<WebView>(R.id.webview)

        webView.loadUrl(args.url)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false

        // Enable Javascript
        // We only allow web client to load pages from our site
        @SuppressWarnings("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true

        // init loading logic
        dataBinding.webProgress.isIndeterminate = true
        dataBinding.webProgress.visibility = VISIBLE

        // Force links and redirects to open in the WebView instead of in a browser
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                try {
                    binding.webProgress.visibility = VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                try {
                    binding.webProgress.visibility = GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                if (Uri.parse(url).host == "www.wsdot.com"
                    || Uri.parse(url).host == "www.wsdot.wa.gov"
                    || Uri.parse(url).host == "www.secureapps.wsdot.wa.gov"
                    || Uri.parse(url).host == "wsdot.com"
                    || Uri.parse(url).host == "wsdot.wa.gov"
                    || Uri.parse(url).host == "secureapps.wsdot.wa.gov") {
                    // This is our web site, so do not override; let my WebView load the page
                    return false
                }

                // Otherwise, the link is not for a page on our site, so launch another Activity that handles URLs
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    if (view?.context != null) {
                        startActivity(view.context, this, null)
                    }
                }

                return true
            }


        }



        // Back button logic
        webView.canGoBack()
        webView.setOnKeyListener(object : View.OnKeyListener {

            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KEYCODE_BACK
                    && event.action == MotionEvent.ACTION_UP
                    && webView.canGoBack()
                ) {
                    webView.goBack()
                    return true
                }
                return false
            }
        })

        return dataBinding.root


    }

}