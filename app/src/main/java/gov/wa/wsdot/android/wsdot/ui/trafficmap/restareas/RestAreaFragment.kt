package gov.wa.wsdot.android.wsdot.ui.trafficmap.restareas

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.RestAreaFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class RestAreaFragment: DaggerFragment(), Injectable, OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var restAreaViewModel: RestAreaViewModel

    var binding by autoCleared<RestAreaFragmentBinding>()

    val args: RestAreaFragmentArgs by navArgs()

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var mMap: GoogleMap

    private var showTrafficLayer: Boolean = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        restAreaViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RestAreaViewModel::class.java)
        val restAreaJson = resources.openRawResource(R.raw.restareas).bufferedReader().use { it.readText() }
        restAreaViewModel.setRestAreaData(restAreaJson)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<RestAreaFragmentBinding>(
            inflater,
            R.layout.rest_area_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        restAreaViewModel.restAreas.observe(viewLifecycleOwner, Observer { restAreas ->
            binding.restArea = (restAreas.filter { it.description == args.description }).first()
        })

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return dataBinding.root
    }

    override fun onMapReady(map: GoogleMap) {

        mMap = map

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

        val settings = PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
        showTrafficLayer = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_traffic_layer), true)


        if(showTrafficLayer) {
            mMap.isTrafficEnabled = true
        }

        mMap.uiSettings.isMapToolbarEnabled = false

        restAreaViewModel.restAreas.observe(viewLifecycleOwner, Observer { restAreas ->

            val restArea = (restAreas.filter { it.description == args.description }).first()

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(restArea.latitude, restArea.longitude), 12.0f))
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(restArea.latitude, restArea.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(restArea.icon)))

        })

    }
}