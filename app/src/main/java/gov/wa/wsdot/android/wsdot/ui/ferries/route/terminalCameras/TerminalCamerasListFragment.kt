package gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraListFragmentBinding
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListAdapter
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingViewModel
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class TerminalCamerasListFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var terminalCamerasViewModel: TerminalCamerasViewModel
    lateinit var sailingViewModel: FerriesSailingViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<CameraListFragmentBinding>()

    private var adapter by autoCleared<CameraListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        terminalCamerasViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TerminalCamerasViewModel::class.java)

        // Get the sailingViewModel for the terminal ID
        sailingViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(FerriesSailingViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<CameraListFragmentBinding>(
            inflater,
            R.layout.camera_list_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                terminalCamerasViewModel.refresh()
            }
        }

        dataBinding.viewModel = terminalCamerasViewModel
        binding = dataBinding

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = CameraListAdapter(dataBindingComponent, appExecutors,
            {
                    camera -> navigateToCamera(camera)
            },
            {
                    terminalCamerasViewModel.updateFavorite(it.cameraId, !it.favorite)
            })

        this.adapter = adapter

        binding.cameraList.adapter = adapter

        postponeEnterTransition()
        binding.cameraList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        // get the departing terminal from the sailingViewModel
        sailingViewModel.sailingsWithStatus.observe(viewLifecycleOwner, Observer { sailings ->
            if (sailings != null) {
                if (sailings.data != null) {
                    if (sailings.data.isNotEmpty()) {
                        terminalCamerasViewModel.setCameraQuery(sailings.data[0].departingTerminalId)
                    }
                }
            }
        })

        terminalCamerasViewModel.terminalCameras.observe(viewLifecycleOwner, Observer { cameras ->
            adapter.submitList(cameras)
            if (cameras.isEmpty()) {
                // TODO: show no cameras message
            }
        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToCamera(camera: Camera){
        val action = NavGraphDirections.actionGlobalNavCameraFragment(camera.cameraId, camera.title)
        findNavController().navigate(action)
    }
}