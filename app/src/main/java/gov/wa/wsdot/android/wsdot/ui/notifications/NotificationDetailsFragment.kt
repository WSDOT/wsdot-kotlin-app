package gov.wa.wsdot.android.wsdot.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.EventDetailsFragmentBinding
import gov.wa.wsdot.android.wsdot.databinding.NotificationDetailsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.autoCleared

class NotificationDetailsFragment: DaggerFragment(), Injectable {

    val args: NotificationDetailsFragmentArgs by navArgs()

    var binding by autoCleared<NotificationDetailsFragmentBinding>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MainActivity).disableAds()

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<NotificationDetailsFragmentBinding>(
            inflater,
            R.layout.notification_details_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        binding.message = args.message

        return dataBinding.root
    }

}
