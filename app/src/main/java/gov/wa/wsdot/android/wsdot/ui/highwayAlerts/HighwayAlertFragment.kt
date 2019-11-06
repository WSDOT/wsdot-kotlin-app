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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
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
import gov.wa.wsdot.android.wsdot.databinding.HighwayAlertFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class HighwayAlertFragment : DaggerFragment(), Injectable, OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var alertViewModel: HighwayAlertViewModel

    var binding by autoCleared<HighwayAlertFragmentBinding>()

    val args: HighwayAlertFragmentArgs by navArgs()

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        alertViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HighwayAlertViewModel::class.java)
        alertViewModel.setAlertQuery(args.alertId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<HighwayAlertFragmentBinding>(
            inflater,
            R.layout.highway_alert_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        alertViewModel.alert.observe(viewLifecycleOwner, Observer { alert ->
            if (alert.data != null) {
                binding.highwayAlert = alert.data
            }
        })

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return dataBinding.root
    }

    override fun onMapReady(map: GoogleMap?) {

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
                mMap.setMapStyle(null)
            }
        }

        mMap.uiSettings.isMapToolbarEnabled = false


        alertViewModel.alert.observe(viewLifecycleOwner, Observer { alert ->
            if (alert.data != null) {

                var alertIcon = BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)

                val construction = arrayOf("construction", "maintenance")
                val closure = arrayOf("closed", "closure")

                when {
                    construction.any { alert.data.category.contains(it, ignoreCase = true) } ->
                        alertIcon = when(alert.data.priority.toLowerCase()) {
                            "highest" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_highest)
                            "high" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_high)
                            "medium" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_moderate)
                            "low" -> BitmapDescriptorFactory.fromResource(R.drawable.construction_low)
                            else -> BitmapDescriptorFactory.fromResource(R.drawable.construction_moderate)
                        }
                    closure.any { alert.data.category.contains(it, ignoreCase = true) } -> {
                        alertIcon = BitmapDescriptorFactory.fromResource(R.drawable.closed)
                    }
                    else -> alertIcon = when(alert.data.priority.toLowerCase()) {
                        "highest" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_highest)
                        "high" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_high)
                        "medium" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                        "low" -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                        else -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                    }
                }

                binding.highwayAlert = alert.data
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(alert.data.startLatitude, alert.data.startLongitude), 14.0f))
                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(alert.data.startLatitude, alert.data.startLongitude))
                        .icon(alertIcon))
            }
        })
    }
}