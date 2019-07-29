package gov.wa.wsdot.android.wsdot.ui.trafficmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.putDouble
import permissions.dispatcher.*
import javax.inject.Inject

@RuntimePermissions
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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


    override fun onPause() {
        super.onPause()
        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = settings.edit()
        editor.putDouble(getString(R.string.user_preference_traffic_map_latitude), mMap.projection.visibleRegion.latLngBounds.center.latitude)
        editor.putDouble(getString(R.string.user_preference_traffic_map_longitude), mMap.projection.visibleRegion.latLngBounds.center.longitude)
        editor.putFloat(getString(R.string.user_preference_traffic_map_zoom), mMap.cameraPosition.zoom)
        editor.apply()
    }

    override fun onMapReady(map: GoogleMap?) {

        mMap = map as GoogleMap

        enableMyLocationWithPermissionCheck()

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val latitude = settings.getDouble(getString(R.string.user_preference_traffic_map_latitude), 47.6062)
        val longitude = settings.getDouble(getString(R.string.user_preference_traffic_map_longitude), -122.3321)
        val zoom = settings.getFloat(getString(R.string.user_preference_traffic_map_zoom), 12.0f)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))

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

                    var alertIcon = BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)

                    val construction = arrayOf("construction", "maintenance")
                    val closure = arrayOf("closed", "closure")

                    when {
                        construction.any { alert.category.contains(it, ignoreCase = true) } ->
                            alertIcon = when(alert.priority.toLowerCase()) {
                                "highest" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_highest)
                                "high" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_high)
                                "medium" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_moderate)
                                "low" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_low)
                                else -> BitmapDescriptorFactory.fromResource(R.drawable.construction_moderate)
                            }
                        closure.any { alert.category.contains(it, ignoreCase = true) } -> {
                            alertIcon = BitmapDescriptorFactory.fromResource(R.drawable.closed)
                        }
                        else -> alertIcon = when(alert.priority.toLowerCase()) {
                            "highest" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_highest)
                            "high" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_high)
                            "medium" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                            "low" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                            else -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                        }
                    }

                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(alert.startLatitude, alert.startLongitude))
                            .visible(showAlerts)
                            .icon(alertIcon))
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


    // Location Permission
    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun enableMyLocation() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        mMap.isMyLocationEnabled = true
                    }
                }
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_map_location_rationale, request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun showRationaleDialog(rationMessage: Int, permRequest: PermissionRequest) {
        context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Location Permission")
            builder.setMessage(rationMessage)
            builder.setCancelable(false)
            builder.setPositiveButton("next") { _, _ -> permRequest.proceed()}
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

}