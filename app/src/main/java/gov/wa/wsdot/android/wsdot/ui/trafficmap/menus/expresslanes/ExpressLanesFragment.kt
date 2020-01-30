package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.expresslanes

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.ExpressLanesFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject

class ExpressLanesFragment: DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var expressLanesViewModel: ExpressLanesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<ExpressLanesFragmentBinding>()

    private var adapter by autoCleared<ExpressLanesListAdapter>()

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

        expressLanesViewModel = ViewModelProvider(this, viewModelFactory)
            .get(ExpressLanesViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<ExpressLanesFragmentBinding>(
            inflater,
            R.layout.express_lanes_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                expressLanesViewModel.refresh()
            }
        }

        dataBinding.viewModel = expressLanesViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = androidx.transition.TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = ExpressLanesListAdapter(dataBindingComponent, appExecutors)

        this.adapter = adapter

        binding.expressLanesList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.expressLanesList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        expressLanesViewModel.expressLanes.observe(viewLifecycleOwner, Observer { expressLanes ->
            adapter.submitList(expressLanes)
        })

        expressLanesViewModel.expressLanesLiveData.observe(viewLifecycleOwner, Observer { resource ->
            if (resource.status == Status.ERROR) {
                Toast.makeText(
                    context,
                    getString(R.string.loading_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_express_lanes, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_express_lanes -> {
                val action = NavGraphDirections.actionGlobalNavWebViewFragment("https://www.wsdot.wa.gov/travel/operations-services/express-lanes/home", "Express Lanes Schedule")
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}