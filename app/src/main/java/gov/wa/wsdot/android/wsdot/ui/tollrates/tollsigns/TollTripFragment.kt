package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TollTripFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.autoCleared

class TollTripFragment : DaggerFragment(), Injectable, OnMapReadyCallback {

    var binding by autoCleared<TollTripFragmentBinding>()

    val args: TollTripFragmentArgs by navArgs()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TollTripFragmentBinding>(
            inflater,
            R.layout.toll_trip_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return dataBinding.root
    }

    override fun onMapReady(map: GoogleMap?) {

        mMap = map as GoogleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.isTrafficEnabled = true

        val startLatitude = args.startLatitude.toDouble()
        val startLongitude = args.startLongitude.toDouble()

        val endLatitude = args.endLatitude.toDouble()
        val endLongitude = args.endLongitude.toDouble()

        val centerPoint = DistanceUtils.getCenterLocation(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude)

        val distance = DistanceUtils.getDistanceFromPoints(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude)

        val zoom = when {
            distance < 3 -> 13.0f
            distance < 4 -> 12.0f
            distance < 9 -> 11.0f
            else -> 10.0f
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, zoom))

        mMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(LatLng(startLatitude, startLongitude)))

        mMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(LatLng(endLatitude, endLongitude)))

    }
}