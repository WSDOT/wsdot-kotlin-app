package gov.wa.wsdot.android.wsdot.ui.ferries

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.databinding.FerriesHomeFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import java.util.logging.Logger
import javax.inject.Inject

import gov.wa.wsdot.android.wsdot.util.autoCleared

class FerriesHomeFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var ferriesViewModel: FerriesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerriesHomeFragmentBinding>()

    private var adapter by autoCleared<FerryScheduleListAdapter>()

    private fun initRoutesList(viewModel: FerriesViewModel) {
        viewModel.routes.observe(viewLifecycleOwner, Observer { routeResource ->
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            Log.e("debug", routeResource.toString())
            if (routeResource?.data != null) {
                adapter.submitList(routeResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
                // TODO: viewModel.retry()
            }
        }
        binding = dataBinding
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FerryScheduleListAdapter(dataBindingComponent, appExecutors) {
                schedule -> Log.e("debug", schedule.description) // TODO() navigation
        }

        this.adapter = adapter

        binding.scheduleList.adapter = adapter

        postponeEnterTransition()

        binding.scheduleList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        initRoutesList(ferriesViewModel)

    }

}