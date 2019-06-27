package gov.wa.wsdot.android.wsdot.ui.mountainpasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionInflater
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassHomeFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class MountainPassHomeFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var passViewModel: MountainPassViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<MountainPassHomeFragmentBinding>()

    private var adapter by autoCleared<MountainPassListAdapter>()

    override fun onDestroy() {
        super.onDestroy()
        // Clear view models since they are no longer needed
        viewModelStore.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        passViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MountainPassViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<MountainPassHomeFragmentBinding>(
            inflater,
            R.layout.mountain_pass_home_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                passViewModel.refresh()
            }
        }

        dataBinding.viewModel = passViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = MountainPassListAdapter(dataBindingComponent, appExecutors,
            {
                    pass -> navigateToRoute(pass.passId, pass.passName)
            },
            {
                    pass -> passViewModel.updateFavorite(pass.passId, !pass.favorite)
            })

        this.adapter = adapter

        binding.passList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.passList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        passViewModel.passes.observe(viewLifecycleOwner, Observer { passResource ->
            if (passResource?.data != null) {
                adapter.submitList(passResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToRoute(routeId: Int, routeName: String) {
       // val action = FerriesHomeFragmentDirections.actionNavFerriesHomeFragmentToNavFerriesRouteFragment(routeId, routeName)
       // findNavController().navigate(action)
    }
}