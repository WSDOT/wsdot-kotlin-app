package gov.wa.wsdot.android.wsdot.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.AboutFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertFragmentArgs
import gov.wa.wsdot.android.wsdot.util.autoCleared
import android.content.Intent
import android.net.Uri
import gov.wa.wsdot.android.wsdot.BuildConfig
import gov.wa.wsdot.android.wsdot.ui.MainActivity


class AboutFragment: DaggerFragment(), Injectable {

    var binding by autoCleared<AboutFragmentBinding>()
    val args: HighwayAlertFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MainActivity).disableAds()

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<AboutFragmentBinding>(
            inflater,
            R.layout.about_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        binding.careersButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.governmentjobs.com/careers/washington/wsdot"))
            startActivity(browserIntent)
        }

        binding.heroButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wsdot.wa.gov/travel/highways-bridges/hov/report-violator"))
            startActivity(browserIntent)
        }

        binding.appBugReportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("webfeedback@wsdot.wa.go"))
            emailIntent.type = "message/rfc822"
            val versionName = BuildConfig.VERSION_NAME

            if (versionName.equals("Not available", true)) {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WSDOT Android App")
            } else {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WSDOT Android App $versionName")
            }
            startActivity(emailIntent)
        }

        binding.appFeedbackButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("webfeedback@wsdot.wa.go"))
            emailIntent.type = "message/rfc822"
            val versionName = BuildConfig.VERSION_NAME

            if (versionName.equals("Not available", true)) {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WSDOT Android App")
            } else {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WSDOT Android App $versionName")
            }
            startActivity(emailIntent)
        }

        binding.ferriesFeedbackButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("wsfinfo@wsdot.wa.gov"))
            emailIntent.type = "message/rfc822"

            startActivity(emailIntent)

        }

        return dataBinding.root
    }

}