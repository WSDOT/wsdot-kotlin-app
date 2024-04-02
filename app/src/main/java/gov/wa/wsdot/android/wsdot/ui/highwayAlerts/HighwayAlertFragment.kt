package gov.wa.wsdot.android.wsdot.ui.highwayAlerts

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.HighwayAlertFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.MapLocationItem
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.trafficmap.MapLocationViewModel
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.model.common.Status
import javax.inject.Inject

class HighwayAlertFragment : DaggerFragment(), Injectable, OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var alertViewModel: HighwayAlertViewModel
    private lateinit var mapLocationViewModel: MapLocationViewModel

    var binding by autoCleared<HighwayAlertFragmentBinding>()

    val args: HighwayAlertFragmentArgs by navArgs()

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var mMap: GoogleMap

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        alertViewModel = ViewModelProvider(this, viewModelFactory)
            .get(HighwayAlertViewModel::class.java)
        alertViewModel.setAlertQuery(args.alertId)

        mapLocationViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MapLocationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<HighwayAlertFragmentBinding>(
            inflater,
            R.layout.highway_alert_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        binding.viewModel = alertViewModel

        alertViewModel.alert.observe(viewLifecycleOwner, Observer { alert ->
            if (alert?.data != null) {
                binding.highwayAlert = alert.data

                alert.data.displayLatitude?.let { alert.data.displayLongitude?.let { it1 ->
                    LatLng(it,
                        it1
                    )
                } }?.let {
                    MapLocationItem(
                        it,
                        12.0f
                    )
                }?.let {
                    mapLocationViewModel.updateLocation(
                        it
                    )
                }


            }

            else if (alert.status != Status.LOADING){
                binding.alertTitle.text = getString(R.string.no_alert_string)
                binding.alertRoadName.visibility = View.GONE
                binding.timestamp.text = ""
            }
        })

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return dataBinding.root
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

        alertViewModel.alert.observe(viewLifecycleOwner, Observer { alert ->
            if (alert?.data != null) {
                mapFragment.view?.visibility = View.VISIBLE

                val alertIcon: BitmapDescriptor = when(alert.data.travelCenterPriorityId) {
                    4 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                    3 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                    2 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_high)
                    1 -> BitmapDescriptorFactory.fromResource(R.drawable.closed)
                    else -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                }

                binding.highwayAlert = alert.data

                var lat = alert.data.displayLatitude
                var long = alert.data.displayLongitude
                var zoom = 12.0f

                if (lat == 0.0 && long == 0.0) {
                    lat = 47.7511
                    long = -120.7401
                    zoom = 6.0f
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat!!, long!!),
                    zoom))
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(lat, long)
                        )
                        .icon(alertIcon))
            } else {
                mapFragment.view?.visibility = View.GONE
            }
        })
    }
}