package gov.wa.wsdot.android.wsdot.ui.trafficmap.bridgeAlerts

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
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.BridgeAlertsFragmentBinding
import gov.wa.wsdot.android.wsdot.db.travelerinfo.BridgeAlert
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class BridgeAlertsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var bridgeAlertsViewModel: BridgeAlertsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<BridgeAlertsFragmentBinding>()

    private var adapter by autoCleared<BridgeAlertListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bridgeAlertsViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(BridgeAlertsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<BridgeAlertsFragmentBinding>(
            inflater,
            R.layout.bridge_alerts_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                bridgeAlertsViewModel.refresh()
            }
        }

        dataBinding.viewModel = bridgeAlertsViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = BridgeAlertListAdapter(dataBindingComponent, appExecutors)
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

        bridgeAlertsViewModel.alerts.observe(viewLifecycleOwner, Observer { alertResource ->

            if (alertResource.data != null) {

                binding.alertList.visibility = VISIBLE
                binding.emptyListView.visibility = GONE

                adapter.submitList(alertResource.data)

                if (alertResource.data.isEmpty()) {
                    binding.emptyListView.text = getString(R.string.no_bridge_alerts_string)
                    binding.alertList.visibility = GONE
                    binding.emptyListView.visibility = VISIBLE
                }

            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToAlert(alert: BridgeAlert){
        val action = NavGraphDirections.actionGlobalNavBridgeAlertFragment(alert.alertId, alert.bridge)
        findNavController().navigate(action)
    }
}