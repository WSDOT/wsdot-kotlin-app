package gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import gov.wa.wsdot.android.wsdot.databinding.MapHighwayAlertsFragmentBinding
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class MapHighwayAlertsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mapHighwayAlertsViewModel: MapHighwayAlertsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<MapHighwayAlertsFragmentBinding>()

    private var adapter by autoCleared<HighwayAlertListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mapHighwayAlertsViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(MapHighwayAlertsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<MapHighwayAlertsFragmentBinding>(
            inflater,
            R.layout.map_highway_alerts_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                mapHighwayAlertsViewModel.refresh()
            }
        }

        dataBinding.viewModel = mapHighwayAlertsViewModel

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

        binding.cameraList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.cameraList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        mapHighwayAlertsViewModel.alerts.observe(viewLifecycleOwner, Observer { alertResource ->

            if (alertResource.data != null) {

                binding.cameraList.visibility = VISIBLE
                binding.emptyListView.visibility = GONE

                adapter.submitList(alertResource.data)

                if (alertResource.data.isEmpty()) {
                    binding.emptyListView.text = getString(R.string.no_alerts_string)
                    binding.cameraList.visibility = GONE
                    binding.emptyListView.visibility = VISIBLE
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