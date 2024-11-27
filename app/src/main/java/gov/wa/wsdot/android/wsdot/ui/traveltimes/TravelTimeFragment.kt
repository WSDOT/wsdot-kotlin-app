package gov.wa.wsdot.android.wsdot.ui.traveltimes

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.MapLocationItem
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class TravelTimeFragment : DaggerFragment(), Injectable, OnMapReadyCallback {

    var binding by autoCleared<TravelTimeFragmentBinding>()

    val args: TravelTimeFragmentArgs by navArgs()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    private var showTrafficLayer: Boolean = true

    private var isFavorite: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var travelTimeViewModel: TravelTimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // set up view models
        travelTimeViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(TravelTimeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        travelTimeViewModel.setRouteId(args.routeId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TravelTimeFragmentBinding>(
            inflater,
            R.layout.travel_time_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding
        dataBinding.viewModel = travelTimeViewModel

        travelTimeViewModel.route.observe(viewLifecycleOwner, Observer { alert ->
            if (alert?.data != null) {
                binding.travelTime = alert.data
                isFavorite = alert.data.favorite
                activity?.invalidateOptionsMenu()

            }
        })
        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return dataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.travel_time_menu, menu)
        setFavoriteMenuIcon(menu.findItem(R.id.action_favorite))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                travelTimeViewModel.updateFavorite(args.routeId)
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

    override fun onMapReady(map: GoogleMap) {

        mMap = map as GoogleMap
        mMap.uiSettings.isMapToolbarEnabled = false

        val settings = PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
        showTrafficLayer = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_traffic_layer), true)

        if(showTrafficLayer) {
            mMap.isTrafficEnabled = true
        }

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

        val startLatitude = args.startLatitude.toDouble()
        val startLongitude = args.startLongitude.toDouble()

        val endLatitude = args.endLatitude.toDouble()
        val endLongitude = args.endLongitude.toDouble()

        val marker1 = LatLng(startLatitude, startLongitude)
        val marker2 = LatLng(endLatitude, endLongitude)

        mMap.addMarker(
            MarkerOptions()
                .position(marker1)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        )

        mMap.addMarker(
            MarkerOptions()
                .position(marker2)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        adjustMapToDisplayMarkers(mMap, marker1, marker2)
}

    private fun adjustMapToDisplayMarkers(googleMap: GoogleMap, marker1: LatLng, marker2: LatLng) {
        val builder = LatLngBounds.Builder()
        builder.include(marker1)
        builder.include(marker2)
        val bounds = builder.build()
        val padding = 125
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.animateCamera(cameraUpdate)
    }

}
