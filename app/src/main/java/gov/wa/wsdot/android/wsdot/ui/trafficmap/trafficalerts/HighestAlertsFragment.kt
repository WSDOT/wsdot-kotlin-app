package gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.HighwayAlertsHighestImpactFragmentBinding
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class HighestAlertsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var highestImpactAlertsViewModel: HighestImpactAlertsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<HighwayAlertsHighestImpactFragmentBinding>()

    private var adapter by autoCleared<HighwayAlertListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        highestImpactAlertsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HighestImpactAlertsViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<HighwayAlertsHighestImpactFragmentBinding>(
            inflater,
            R.layout.highway_alerts_highest_impact_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                highestImpactAlertsViewModel.refresh()
            }
        }

        dataBinding.viewModel

        dataBinding.viewModel = highestImpactAlertsViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = HighwayAlertListAdapter(dataBindingComponent, appExecutors)
        { alert -> navigateToAlert(alert) }

        this.adapter = adapter

        binding.alertList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.alertList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        highestImpactAlertsViewModel.highestImpactAlerts.observe(viewLifecycleOwner, Observer { alertResource ->

            if (alertResource.data != null) {

                binding.alertList.visibility = View.VISIBLE
                binding.emptyListView.visibility = View.GONE

                adapter.submitList(alertResource.data)

                if (alertResource.data.isEmpty()) {
                    binding.emptyListView.text = getString(R.string.no_highest_alerts_string)
                    binding.alertList.visibility = View.GONE
                    binding.emptyListView.visibility = View.VISIBLE
                }

            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToAlert(alert: HighwayAlert){
        val action = NavGraphDirections.actionGlobalNavHighwayAlertFragment(alert.alertId, alert.category)
        findNavController().navigate(action)
    }
}