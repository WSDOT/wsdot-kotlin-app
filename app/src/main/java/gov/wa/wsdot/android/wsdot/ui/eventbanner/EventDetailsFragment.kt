package gov.wa.wsdot.android.wsdot.ui.eventbanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.EventDetailsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.MainViewModel
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class EventDetailsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel

    var binding by autoCleared<EventDetailsFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MainActivity).disableAds()

        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainViewModel::class.java)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<EventDetailsFragmentBinding>(
            inflater,
            R.layout.event_details_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        mainViewModel.eventStatus.observe(viewLifecycleOwner, Observer { eventResponse ->
            binding.event = eventResponse.data
        })

        return dataBinding.root
    }

}