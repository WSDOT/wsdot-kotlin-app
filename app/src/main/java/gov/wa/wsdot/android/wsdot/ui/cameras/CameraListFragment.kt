package gov.wa.wsdot.android.wsdot.ui.cameras

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
import gov.wa.wsdot.android.wsdot.databinding.CameraListFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback

import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class CameraListFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var camerasViewModel: CameraListViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<CameraListFragmentBinding>()

    private var adapter by autoCleared<CameraListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       camerasViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CameraListViewModel::class.java)


        val dataBinding = DataBindingUtil.inflate<CameraListFragmentBinding>(
            inflater,
            R.layout.camera_list_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                camerasViewModel.refresh()
            }
        }

        dataBinding.viewModel = camerasViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = CameraListAdapter(dataBindingComponent, appExecutors,
            {
                    camera -> navigateToCamera(camera.cameraId)
            },
            {

            })

        this.adapter = adapter

        binding.cameraList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.cameraList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        camerasViewModel.cameras.observe(viewLifecycleOwner, Observer { cameraResource ->
            if (cameraResource.data != null) {
                adapter.submitList(cameraResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToCamera(cameraId: Int){
       // val action = CameraListFragmentDirections.actionNavFerriesHomeFragmentToNavFerriesRouteFragment(cameraId)
       // findNavController().navigate(action)
    }
}