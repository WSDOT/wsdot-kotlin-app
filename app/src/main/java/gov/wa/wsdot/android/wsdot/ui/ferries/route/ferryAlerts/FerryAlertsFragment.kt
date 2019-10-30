package gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerryAlertsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject

class FerryAlertsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var ferryAlertsViewModel: FerryAlertsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerryAlertsFragmentBinding>()

    private var adapter by autoCleared<FerryAlertsListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        ferryAlertsViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(FerryAlertsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<FerryAlertsFragmentBinding>(
            inflater,
            R.layout.ferry_alerts_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                ferryAlertsViewModel.refresh()
            }
        }

        dataBinding.viewModel = ferryAlertsViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = FerryAlertsListAdapter(dataBindingComponent, appExecutors)

        this.adapter = adapter

        binding.alertList.adapter = adapter

        postponeEnterTransition()
        binding.alertList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        ferryAlertsViewModel.ferryAlerts.observe(viewLifecycleOwner, Observer { alertsResource ->
            if (alertsResource?.data != null) {
                if (alertsResource.status != Status.ERROR && alertsResource.status != Status.LOADING) {

                    if (alertsResource.data.isEmpty()) {
                        binding.emptyListView.visibility = View.VISIBLE
                    } else {
                        binding.emptyListView.visibility = View.GONE
                    }

                    adapter.submitList(alertsResource.data)

                }

            } else {
                adapter.submitList(emptyList())
            }
        })
    }

}