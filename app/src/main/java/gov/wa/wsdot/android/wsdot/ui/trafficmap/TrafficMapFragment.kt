package gov.wa.wsdot.android.wsdot.ui.trafficmap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.cameras.CamerasViewModel
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertsViewModel
import javax.inject.Inject

class TrafficMapFragment : DaggerFragment(), Injectable , OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var highwayAlertsViewModel: HighwayAlertsViewModel
    lateinit var camerasViewModel: CamerasViewModel

    private val highwayAlertMarkers = HashMap<Marker, HighwayAlert>()
    private val cameraMarkers = HashMap<Marker, Camera>()

    var showAlerts: Boolean = true
    var showCameras: Boolean = true

    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.map_fragment, container, false)

        highwayAlertsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HighwayAlertsViewModel::class.java)

        camerasViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CamerasViewModel::class.java)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(map: GoogleMap?) {

        mMap = map as GoogleMap

        val seattle = LatLng(47.6062, -122.3321)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seattle, 12.0f))

        mMap.setOnMarkerClickListener(this)

        highwayAlertsViewModel.alerts.observe(viewLifecycleOwner, Observer { alerts ->
            if (alerts.data != null) {

                with(highwayAlertMarkers.iterator()) {
                    forEach {
                        it.key.remove()
                        remove()
                    }
                }

                for (alert in alerts.data) {
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(alert.startLatitude, alert.startLongitude))
                            .visible(showAlerts)
                            .icon(when(alert.priority.toLowerCase()) {
                                "highest" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_highest)
                                "high" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_high)
                                "medium" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                                "low" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                                else -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                            }))
                    highwayAlertMarkers[marker] = alert
                }
            }
        })

        camerasViewModel.cameras.observe(viewLifecycleOwner, Observer { cameras ->
            if (cameras.data != null) {

                with(cameraMarkers.iterator()) {
                    forEach {
                        it.key.remove()
                        remove()
                    }
                }

                for (camera in cameras.data) {
                    val marker = mMap.addMarker(MarkerOptions()
                        .position(LatLng(camera.latitude, camera.longitude))
                        .visible(showCameras)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)))
                    cameraMarkers[marker] = camera

                }
            }
        })
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        highwayAlertMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavHighwayAlertFragment(it.alertId, it.category)
            findNavController().navigate(action)
            return true
        }

        cameraMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavCameraFragment(it.cameraId, it.title)
            findNavController().navigate(action)
            return true
        }

        return true
    }
}