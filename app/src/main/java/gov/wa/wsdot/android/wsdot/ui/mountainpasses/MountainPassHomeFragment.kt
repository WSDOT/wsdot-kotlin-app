package gov.wa.wsdot.android.wsdot.ui.mountainpasses

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.TransitionInflater
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassHomeFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.model.common.Status
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

    private lateinit var toast: Toast
    private var isFavorite: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear view models since they are no longer needed
        viewModelStore.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val adTargets = mapOf("wsdotapp" to resources.getString(R.string.ad_target_passes))
//        (activity as MainActivity).enableAds(adTargets)

        (activity as MainActivity).disableAds()

        passViewModel = ViewModelProvider(this, viewModelFactory)
            .get(MountainPassViewModel::class.java)

        passViewModel.refresh()

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
        sharedElementReturnTransition = context?.let { TransitionInflater.from(it).inflateTransition(R.transition.move) }

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = MountainPassListAdapter(dataBindingComponent, appExecutors,
            {
                    pass -> navigateToPassReport(pass.passId, pass.passName)
            },
            {
                pass -> passViewModel.updateFavorite(pass.passId, !pass.favorite)

                isFavorite = pass.favorite

                if (this::toast.isInitialized)
                {
                    toast.cancel()
                }

                if (!isFavorite) {
                    toast = Toast.makeText(context, getString(R.string.favorite_added_message), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }
                else {
                    toast = Toast.makeText(context, getString(R.string.favorite_removed_message), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }
            })

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.passList.addItemDecoration(itemDivider)

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

            if (passResource.status == Status.ERROR) {
                Toast.makeText(context, getString(R.string.loading_error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToPassReport(routeId: Int, routeName: String) {
        if (findNavController().currentDestination?.id != R.id.navMountainPassReportFragment) {
            val action =
                MountainPassHomeFragmentDirections.actionNavMountainPassHomeFragmentToNavMountainPassReportFragment(
                    routeId,
                    routeName
                )
            findNavController().navigate(action)
        }
    }

}