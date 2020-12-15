package gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerryAlertItemBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class FerryAlertDetailsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var ferryAlertDetailsViewModel: FerryAlertDetailsViewModel

    var binding by autoCleared<FerryAlertItemBinding>()

    val args: FerryAlertDetailsFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        ferryAlertDetailsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(FerryAlertDetailsViewModel::class.java)
        ferryAlertDetailsViewModel.setFerryAlertRouteQuery(args.alertId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<FerryAlertItemBinding>(
            inflater,
            R.layout.ferry_alert_item,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        ferryAlertDetailsViewModel.ferryAlert.observe(viewLifecycleOwner, Observer { alert ->
            // Is data not null when alerts have expired?
            if (alert?.data != null) {
                binding.alert = alert.data
            } else {
                binding.titleView.text = "Alert Unavailable"
            }
        })

        return dataBinding.root
    }

}