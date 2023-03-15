package gov.wa.wsdot.android.wsdot.ui.cameras

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import java.util.*
import javax.inject.Inject


/**
 * Fragment displays a single camera item with data from CameraViewModel
 * Refreshes camera image every 5 minutes.
 * Allows user to mark a camera as a favorite in their favorites list.
 */
class CameraFragment : DaggerFragment(), Injectable, OnMapReadyCallback {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var cameraViewModel: CameraViewModel

    val args: CameraFragmentArgs by navArgs()

    private var isFavorite: Boolean = false

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    var binding by autoCleared<CameraFragmentBinding>()

    // Camera update task timer
    var t: Timer? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        cameraViewModel = ViewModelProvider(this, viewModelFactory)
            .get(CameraViewModel::class.java)
        cameraViewModel.setCameraQuery(args.cameraId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<CameraFragmentBinding>(
            inflater,
            R.layout.camera_fragment,
            container,
            false
        )

        cameraViewModel.camera.observe(viewLifecycleOwner, Observer { camera ->
            if (camera.data != null) {
                isFavorite = camera.data.favorite
                activity?.invalidateOptionsMenu()
            }
        })

        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.cameraViewModel = cameraViewModel

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding = dataBinding

        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        t = Timer()
        t?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    appExecutors.mainThread().execute {
                        binding.invalidateAll()
                    }
                }
            },
            300000,
            300000
        )

    }

    override fun onPause() {
        super.onPause()
        t?.cancel()
    }

    override fun onMapReady(map: GoogleMap) {

        mMap = map as GoogleMap

        context?.let {
            if (NightModeConfig.nightModeOn(it)) {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            it, R.raw.map_night_style))

                } catch (e: Resources.NotFoundException) {
                    Log.e("debug", "Can't find style. Error: ", e)
                }

            } else {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    val success: Boolean = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            it, R.raw.googlemapstyle
                        )
                    )
                    if (!success) {
                        Log.e("debug", "Style parsing failed.")
                        mMap.setMapStyle(null)

                    } else {
                        Log.e("debug", "Style parsing failed.")

                    }
                } catch (e: Resources.NotFoundException) {
                    Log.e("debug", "Can't find style. Error: ", e)
                }
            }
        }


        mMap.uiSettings.isMapToolbarEnabled = false

        cameraViewModel.camera.observe(viewLifecycleOwner, Observer { cameraResponse ->
            if (cameraResponse.data != null) {

                val icon = BitmapDescriptorFactory.fromResource(R.drawable.camera)
                val cameraLocation = LatLng(cameraResponse.data.latitude, cameraResponse.data.longitude)

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 14.0f))
                mMap.addMarker(
                    MarkerOptions()
                        .position(cameraLocation)
                        .icon(icon))
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.camera_menu, menu)
        setFavoriteMenuIcon(menu.findItem(R.id.action_favorite))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                cameraViewModel.updateFavorite(args.cameraId)
                return false
            }
            else -> {}
        }
        return false
    }

    private fun setFavoriteMenuIcon(menuItem: MenuItem){
        if (isFavorite) {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_pink, null)
        } else {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_outline, null)
        }
    }

}