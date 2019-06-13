package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

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
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject


class VesselWatchFragment: DaggerFragment(), Injectable, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var vesselViewModel: VesselWatchViewModel

    var binding by autoCleared<VesselWatchBinding>()

    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private val vesselMarkers = HashMap<Marker, Vessel>()

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


        // bind view models to view
        binding = dataBinding


        return dataBinding.root
    }


    override fun onMapReady(map: GoogleMap?) {

        mMap = map as GoogleMap

        val seattle = LatLng(47.6062, -122.3321)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seattle, 12.0f))
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
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        val vessel = vesselMarkers[marker]

        if (vessel != null) {
            val action = VesselWatchFragmentDirections.actionNavVesselWatchFragmentToNavVesselDetailsFragment(vessel.vesselId, vessel.vesselName)
            findNavController().navigate(action)
        }


        // TODO: Cameras

        return true
    }


}