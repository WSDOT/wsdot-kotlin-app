package gov.wa.wsdot.android.wsdot.ui.trafficmap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.di.Injectable

class TrafficMapFragment : DaggerFragment(), Injectable , OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.map_fragment, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(map: GoogleMap?) {

        System.err.println("OnMapReady start")
        mMap = map as GoogleMap

        val seattle = LatLng(47.6062, -122.3321)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seattle, 12.0f))

    }

}