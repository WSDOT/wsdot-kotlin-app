package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation.bridgeAlerts

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
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
import gov.wa.wsdot.android.wsdot.model.common.Status
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import kotlinx.android.synthetic.main.bridge_alerts_fragment.*
import java.util.*
import javax.inject.Inject

class BridgeAlertsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var bridgeAlertsViewModel: BridgeAlertsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<BridgeAlertsFragmentBinding>()

    private var hoodCanalBridgeAdapter by autoCleared<BridgeAlertListAdapter>()
    private var firstAveBridgeAdapter by autoCleared<BridgeAlertListAdapter>()
    private var interstateBridgeAdapter by autoCleared<BridgeAlertListAdapter>()
    private var newBridgeAdapter by autoCleared<BridgeAlertListAdapter>()

    // Toast
    private lateinit var toast: Toast

    lateinit var t: Timer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        bridgeAlertsViewModel.refresh()

        // animation
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onPause() {
        super.onPause()
        t.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val hoodCanalBridgeAdapter = BridgeAlertListAdapter(dataBindingComponent, appExecutors)
        { alert -> navigateToAlert(alert) }
        this.hoodCanalBridgeAdapter = hoodCanalBridgeAdapter
        binding.hoodCanalBridgeList.adapter = hoodCanalBridgeAdapter

        val firstAveBridgeAdapter = BridgeAlertListAdapter(dataBindingComponent, appExecutors)
        { alert -> navigateToAlert(alert) }
        this.firstAveBridgeAdapter = firstAveBridgeAdapter
        binding.firstAveBridgeList.adapter = firstAveBridgeAdapter

        val interstateBridgeAdapter = BridgeAlertListAdapter(dataBindingComponent, appExecutors)
        { alert -> navigateToAlert(alert) }
        this.interstateBridgeAdapter = interstateBridgeAdapter
        binding.interstateBridgeList.adapter = interstateBridgeAdapter

        val newBridgeAdapter = BridgeAlertListAdapter(dataBindingComponent, appExecutors)
        { alert -> navigateToAlert(alert) }
        this.newBridgeAdapter = newBridgeAdapter
        binding.newBridgeList.adapter = newBridgeAdapter

        // animations
        postponeEnterTransition()
        binding.hoodCanalBridgeList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        binding.firstAveBridgeList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        binding.interstateBridgeList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        binding.newBridgeList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        bridgeAlertsViewModel.alerts.observe(viewLifecycleOwner, Observer { alertResource ->
            val hoodCanalBridgeList: MutableList<BridgeAlert> = mutableListOf()
            val firstAveBridgeList: MutableList<BridgeAlert> = mutableListOf()
            val interstateBridgeList: MutableList<BridgeAlert> = mutableListOf()
            val newBridgeList: MutableList<BridgeAlert> = mutableListOf()

            when (alertResource.status) {
                Status.LOADING -> {
                    binding.bridgeLayout.visibility = GONE
                }
                Status.ERROR -> {
                        binding.bridgeLayout.visibility = GONE
                        firstAveBridgeAdapter.submitList(emptyList())
                        hoodCanalBridgeAdapter.submitList(emptyList())
                        interstateBridgeAdapter.submitList(emptyList())
                        newBridgeAdapter.submitList(emptyList())
                        showToast("Error Loading")
                }
                Status.SUCCESS -> {
                    if (alertResource.data != null) {
                        binding.bridgeLayout.visibility = VISIBLE
                        for (bridge in alertResource.data) {
                            if (bridge.bridge == "Hood Canal") {
                                bridge.bridge = "Hood Canal Bridge"
                                hoodCanalBridgeList.add(bridge)
                                bridgeAlerts("Hood Canal", true)
                            }
                        }
                        if (hoodCanalBridgeList.isEmpty()) {
                            bridgeAlerts("Hood Canal", false)
                        }
                        for (bridge in alertResource.data) {
                            if (bridge.bridge == "1st Avenue South Bridge") {
                                firstAveBridgeList.add(bridge)
                                bridgeAlerts("1st Avenue South Bridge", true)
                            }
                        }
                        if (firstAveBridgeList.isEmpty()) {
                            bridgeAlerts("1st Avenue South Bridge", false)
                        }
                        for (bridge in alertResource.data) {
                            if (bridge.bridge == "Interstate Bridge") {
                                interstateBridgeList.add(bridge)
                                bridgeAlerts("Interstate Bridge", true)
                            }
                        }
                        if (interstateBridgeList.isEmpty()) {
                            bridgeAlerts("Interstate Bridge", false)
                        }

                        for (bridge in alertResource.data) {
                            if (bridge.bridge != "Hood Canal Bridge" && bridge.bridge != "1st Avenue South Bridge" && bridge.bridge != "Interstate Bridge") {
                                newBridgeList.add(bridge)
                                bridgeAlerts(bridge.bridge, true)
                                new_bridge_header.visibility = VISIBLE

                            }
                        }
                        if (newBridgeList.isEmpty()) {
                            bridgeAlerts("", false)
                            new_bridge_header.visibility = GONE

                        }

                        hoodCanalBridgeAdapter.submitList(hoodCanalBridgeList.sortedByDescending{it.lastUpdatedTime})
                        firstAveBridgeAdapter.submitList(firstAveBridgeList.sortedByDescending{it.lastUpdatedTime})
                        interstateBridgeAdapter.submitList(interstateBridgeList.sortedByDescending{it.lastUpdatedTime})
                        newBridgeAdapter.submitList(newBridgeList)

                    }
                    else {
                        binding.bridgeLayout.visibility = GONE
                        firstAveBridgeAdapter.submitList(emptyList())
                        hoodCanalBridgeAdapter.submitList(emptyList())
                        interstateBridgeAdapter.submitList(emptyList())
                        newBridgeAdapter.submitList(emptyList())
                        showToast("Error Loading")
                    }
                }
            }
        })

        startBridgeAlertTask()

    }

    private fun bridgeAlerts(bridge: String, visible: Boolean) {

        if (visible) {
            when (bridge) {
                "Hood Canal" -> {
                    binding.hoodCanalBridgeList.visibility = VISIBLE
                    binding.hoodCanalBridgeEmptyView.visibility = GONE
                    binding.hoodCanalBridgeEmptyListView.visibility = GONE
                }
                "1st Avenue South Bridge" -> {
                    binding.firstAveBridgeList.visibility = VISIBLE
                    binding.firstAveBridgeEmptyView.visibility = GONE
                    binding.firstAveBridgeEmptyListView.visibility = GONE
                }
                "Interstate Bridge" -> {
                    binding.interstateBridgeList.visibility = VISIBLE
                    binding.interstateBridgeEmptyView.visibility = GONE
                    binding.interstateBridgeEmptyListView.visibility = GONE
                }

                "Bridge Alerts" -> {
                    binding.newBridgeList.visibility = VISIBLE
                    binding.newBridgeEmptyView.visibility = GONE
                    binding.newBridgeEmptyListView.visibility = GONE
                }
            }
        } else {
            when (bridge) {
                "Hood Canal" -> {
                    binding.hoodCanalBridgeEmptyListView.text =
                        getString(R.string.no_bridge_alerts_string)
                    binding.hoodCanalBridgeEmptyView.visibility = VISIBLE
                    binding.hoodCanalBridgeEmptyListView.visibility = VISIBLE
                }
                "1st Avenue South Bridge" -> {
                    binding.firstAveBridgeEmptyListView.text = getString(R.string.no_bridge_alerts_string)
                    binding.firstAveBridgeEmptyView.visibility = VISIBLE
                    binding.firstAveBridgeEmptyListView.visibility = VISIBLE
                }
                "Interstate Bridge" -> {
                    binding.interstateBridgeEmptyListView.text =
                        getString(R.string.no_bridge_alerts_string)
                    binding.interstateBridgeEmptyView.visibility = VISIBLE
                    binding.interstateBridgeEmptyListView.visibility = VISIBLE

                }
                "Bridge Alerts" -> {
                    binding.newBridgeEmptyListView.text =
                        getString(R.string.no_bridge_alerts_string)
                    binding.newBridgeEmptyView.visibility = VISIBLE
                    binding.newBridgeEmptyListView.visibility = VISIBLE

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bridge_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                bridgeAlertsViewModel.refresh()
                showToast("refreshing...")
                return false
            }
            else -> {}
        }
        return false
    }

    private fun showToast(message: String) {
        if (this::toast.isInitialized)
        {
            toast.cancel()
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,500)
        toast.show()
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToAlert(alert: BridgeAlert){
        val action = NavGraphDirections.actionGlobalNavBridgeAlertFragment(alert.alertId, alert.bridge)
        findNavController().navigate(action)
    }

    private fun startBridgeAlertTask() {
        t = Timer()
        t.schedule(
            object : TimerTask() {
                override fun run() {
                    appExecutors.mainThread().execute {
                        bridgeAlertsViewModel.refresh()
                    }
                }
            },
            60000,
            120000
        )

    }

}