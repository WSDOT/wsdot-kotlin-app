package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingComponent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TravelChartsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class TravelChartsFragment: DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var travelChartsViewModel: TravelChartsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    private var adapter by autoCleared<TravelChartListAdapter>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<TravelChartsFragmentBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MainActivity).disableAds()

        travelChartsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(TravelChartsViewModel::class.java)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TravelChartsFragmentBinding>(
            inflater,
            R.layout.travel_charts_fragment,
            container,
            false
        )

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = travelChartsViewModel

        this.adapter = TravelChartListAdapter(dataBindingComponent, appExecutors)

        binding.travelChartList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.travelChartList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        travelChartsViewModel.displayedTravelCharts.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

    }

}