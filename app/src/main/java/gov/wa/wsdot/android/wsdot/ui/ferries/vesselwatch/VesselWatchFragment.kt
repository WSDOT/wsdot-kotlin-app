package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.VesselWatchBinding
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject
import android.preference.PreferenceManager
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.putDouble


class VesselWatchFragment: DaggerFragment(), Injectable, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var vesselViewModel: VesselWatchViewModel

    var binding by autoCleared<VesselWatchBinding>()

    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private val vesselMarkers = HashMap<Marker, Vessel>()
    private val cameraMarkers = HashMap<Marker, Camera>()

    var showCameras: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set up view models
        vesselViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(VesselWatchViewModel::class.java)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<VesselWatchBinding>(
            inflater,
            R.layout.vessel_watch,
            container,
            false)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        showCameras = settings.getBoolean("KEY_VESSEL_WATCH_CAMERA_VISIBILITY", true)

        vesselViewModel.setShowCameras(showCameras)

        dataBinding.vesselViewModel = vesselViewModel

        dataBinding.lifecycleOwner = this

        binding = dataBinding

        return dataBinding.root
    }


    override fun onMapReady(map: GoogleMap?) {

        mMap = map as GoogleMap

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)

        val latitude = settings.getDouble("KEY_VESSEL_WATCH_LAT", 47.583571)
        val longitude = settings.getDouble("KEY_VESSEL_WATCH_LON", -122.473468)
        val zoom = settings.getFloat("KEY_VESSEL_WATCH_ZOOM", 10f)

        val startLocation = LatLng(latitude, longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, zoom))
        mMap.setOnMarkerClickListener(this)

        vesselViewModel.vessels.observe(viewLifecycleOwner, Observer { vessels ->
            if (vessels.data != null) {

                // loop over vessel vesselMarkers, removing any old ones from google map and vesselMarkers hash map
                with(vesselMarkers.iterator()) {
                    forEach {
                            it.key.remove()
                            remove()
                    }
                }

                for (vessel in vessels.data) {

                    if (vessel.inService) {
                        val stopped = vessel.speed < 0.5
                        val marker = mMap.addMarker(MarkerOptions()
                            .position(LatLng(vessel.latitude, vessel.longitude))
                            .rotation(if (stopped) 0f else vessel.heading.toFloat())
                            .icon(BitmapDescriptorFactory.fromResource(if (stopped) R.drawable.stopped else R.drawable.ferry_0)))
                        vesselMarkers[marker] = vessel

                    }
                }
            }
        })


        vesselViewModel.cameras.observe(viewLifecycleOwner, Observer { cameras ->
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

        binding.cameraVisibilityFab.setOnClickListener {
            val editor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
            editor.putBoolean("KEY_VESSEL_WATCH_CAMERA_VISIBILITY", !showCameras)
            editor.apply()
            showCameras = !showCameras
            vesselViewModel.setShowCameras(showCameras)

            // loop over camera markers, setting viability
            with(cameraMarkers.iterator()) {
                forEach {
                    it.key.isVisible = showCameras
                }
            }

        }

    }

    override fun onPause() {
        super.onPause()
        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = settings.edit()
        editor.putDouble("KEY_VESSEL_WATCH_LAT", mMap.projection.visibleRegion.latLngBounds.center.latitude)
        editor.putDouble("KEY_VESSEL_WATCH_LON", mMap.projection.visibleRegion.latLngBounds.center.longitude)
        editor.putFloat("KEY_VESSEL_WATCH_ZOOM", mMap.cameraPosition.zoom)
        editor.apply()
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        val vessel = vesselMarkers[marker]

        if (vessel != null) {
            val action = VesselWatchFragmentDirections.actionNavVesselWatchFragmentToNavVesselDetailsFragment(vessel.vesselId, vessel.vesselName)
            findNavController().navigate(action)
            return true
        }

        val camera = cameraMarkers[marker]
        if (camera != null) {
            val action = NavGraphDirections.actionGlobalNavCameraFragment(camera.cameraId, camera.title)
            findNavController().navigate(action)
            return true
        }

        return true
    }


}