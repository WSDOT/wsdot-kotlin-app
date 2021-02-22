package gov.wa.wsdot.android.wsdot.ui.eventbanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.EventDetailsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

/**
 * Fragment displays dynamic event banner details.
 * The event banner displays in the navigation bar when
 * activated. Activation occurs when server data is set
 * for the app to display, otherwise it's hidden
 */
class EventDetailsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: EventBannerViewModel

    var binding by autoCleared<EventDetailsFragmentBinding>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MainActivity).disableAds()

        mainViewModel = ViewModelProvider(this, viewModelFactory)
            .get(EventBannerViewModel::class.java)

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

            // mark current event as seen
            eventResponse.data?.let {event ->
                context?.let {context ->
                    val settings = PreferenceManager.getDefaultSharedPreferences(context)
                    val editor = settings.edit()
                    editor.putString(getString(R.string.pref_key_last_seen_event), event.title)
                    editor.apply()
                }
            }
        })

        return dataBinding.root
    }

}