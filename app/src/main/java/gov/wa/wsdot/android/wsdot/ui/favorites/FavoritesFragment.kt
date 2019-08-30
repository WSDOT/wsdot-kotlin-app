package gov.wa.wsdot.android.wsdot.ui.favorites

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraListFragmentBinding
import gov.wa.wsdot.android.wsdot.databinding.FavoritesListFragmentBinding
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListAdapter
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListFragmentArgs
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListViewModel
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class FavoritesFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var favoritesListViewModel: FavoritesListViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FavoritesListFragmentBinding>()

    private var adapter by autoCleared<FavoritesListAdapter>()

    val args: CameraListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FavoritesListViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<FavoritesListFragmentBinding>(
            inflater,
            R.layout.favorites_list_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                favoritesListViewModel.refresh()
            }
        }

        dataBinding.viewModel = favoritesListViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = FavoritesListAdapter(dataBindingComponent, appExecutors,
            {
                    travelTime -> navigateToTravelTime(travelTime)
            },
            {
                    camera -> navigateToCamera(camera)
            })

        this.adapter = adapter

        binding.favoritesList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.favoritesList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        favoritesListViewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->

            adapter.submitList(favorites)

        })
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToCamera(camera: Camera){
        val action = NavGraphDirections.actionGlobalNavCameraFragment(camera.cameraId, camera.title)
        findNavController().navigate(action)
    }

    private fun navigateToTravelTime(travelTime: TravelTime){
        //  val action = NavGraphDirections.actionGlobalNavTravelTimeFragment(travelTime.travelTimeId, travelTime.title)
        //  findNavController().navigate(action)
    }
}