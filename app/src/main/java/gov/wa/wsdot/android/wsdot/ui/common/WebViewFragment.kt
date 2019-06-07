package gov.wa.wsdot.android.wsdot.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesHomeFragmentBinding
import gov.wa.wsdot.android.wsdot.databinding.WebViewFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment


class WebViewFragment: Fragment(), BackFragment {

    var binding by autoCleared<FerriesHomeFragmentBinding>()

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

        val webView = dataBinding.root.findViewById<WebView>(R.id.webview)

        webView.loadUrl(args.url)

        // Enable Javascript
        webView.settings.setJavaScriptEnabled(true)

        // Force links and redirects to open in the WebView instead of in a browser
        webView.webViewClient = WebViewClient()
        
        // TODO
        //activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, OnBackPressedCallback {

        return dataBinding.root

    }

    override fun allowBack(): Boolean {



        return true


    }
}