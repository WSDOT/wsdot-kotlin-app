package gov.wa.wsdot.android.wsdot.ui.ferries

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesHomeFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject

class FerriesHomeFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var ferriesViewModel: FerriesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerriesHomeFragmentBinding>()

    private var adapter by autoCleared<FerryScheduleListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear view models since they are no longer needed
        activity?.viewModelStore?.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as MainActivity).enableAds(resources.getString(R.string.ad_target_ferries))

        ferriesViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<FerriesHomeFragmentBinding>(
            inflater,
            R.layout.ferries_home_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                ferriesViewModel.refresh()
            }
        }

        dataBinding.viewModel = ferriesViewModel

        dataBinding.reservationButton.setOnClickListener {
            val action = FerriesHomeFragmentDirections.actionNavFerriesRouteFragmentToNavWebViewFragment("https://secureapps.wsdot.wa.gov/ferries/reservations/vehicle/Mobile/MobileDefault.aspx", "Reservations")
            findNavController().navigate(action)
        }

        dataBinding.buyTicketsButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wave2go.wsdot.com/webstore/landingPage?cg=21&c=76"))
            startActivity(browserIntent)
        }

        dataBinding.vesselWatchButton.setOnClickListener {
            val action = FerriesHomeFragmentDirections.actionNavFerriesRouteFragmentToNavVesselWatchFragment()
            findNavController().navigate(action)
        }

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = FerryScheduleListAdapter(dataBindingComponent, appExecutors,
            {
                schedule -> navigateToRoute(schedule.routeId, schedule.description)
            },
            {
                schedule -> ferriesViewModel.updateFavorite(schedule.routeId, !schedule.favorite)
            })

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.scheduleList.addItemDecoration(itemDivider)

        binding.scheduleList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.scheduleList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        ferriesViewModel.routes.observe(viewLifecycleOwner, Observer { routeResource ->
            if (routeResource?.data != null) {
                adapter.submitList(routeResource.data)
            } else {
                adapter.submitList(emptyList())
            }

            if (routeResource.status == Status.ERROR) {
                Toast.makeText(context, getString(R.string.loading_error_message), Toast.LENGTH_SHORT).show()
            }


        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToRoute(routeId: Int, routeName: String) {
        val action = FerriesHomeFragmentDirections.actionNavFerriesHomeFragmentToNavFerriesRouteFragment(routeId, routeName)
        findNavController().navigate(action)
    }
}