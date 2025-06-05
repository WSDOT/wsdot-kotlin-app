package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.VesselWatchBinding
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.common.Status
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraViewModel
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.putDouble
import permissions.dispatcher.NeedsPermission
import javax.inject.Inject

class VesselWatchFragment: DaggerFragment(), Injectable, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var vesselViewModel: VesselWatchViewModel
    lateinit var cameraViewModel: CameraViewModel

    var binding by autoCleared<VesselWatchBinding>()

    private var mMap: GoogleMap? = null

    private lateinit var mapFragment: SupportMapFragment

    private val vesselMarkers = HashMap<Marker, Vessel>()
    private val cameraMarkers = HashMap<Marker, Camera>()
    private val terminalMarkers = HashMap<Marker, Vessel>()
    private val vesselLabels = HashMap<Marker, Vessel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var showCameras: Boolean = true
    private var selectedCameraMarker: Marker? = null
    var requestLocation: Boolean = true

    val bitmap = createBitmap(70, 35)
    var showTrafficLayer: Boolean = true
    var showTerminalLayer: Boolean = true

    private lateinit var toast: Toast

    // FAB
    private lateinit var mFab: SpeedDialView

    val bitmap = createBitmap(70, 30)
    val canvas = Canvas(bitmap)
    val text = Paint()
    val background = Paint()

    private val vesselNames = mapOf("Cathlamet" to "CAT", "Chelan" to "CHE", "Chetzemoka" to "CHZ", "Issaquah" to "ISS",
    "Kaleetan" to "KAL", "Kennewick" to "KEN", "Kitsap" to "KIS", "Kittitas" to "KIT", "Puyallup" to "PUY", "Salish" to "SAL", "Samish" to "SAM",
    "Sealth" to "SEA", "Spokane" to "SPO", "Suquamish" to "SUQ", "Tacoma" to "TAC", "Tillikum" to "TIL", "Tokitae" to "TOK", "Walla Walla" to "WAL",
        "Yakima" to "YAK")

    private lateinit var vesselUpdateHandler: Handler
    private val vesselUpdateTask = object: Runnable {
        override fun run() {
            vesselViewModel.refresh()
            vesselUpdateHandler.postDelayed(this, 30000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                myLocationFine()
                println("Precise location access granted.")

            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                myLocationCoarse()
                println("Coarse location access granted.")

            }
            else -> {
                println("No location access granted.")
                requestLocation = false

            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vesselUpdateHandler = Handler(Looper.getMainLooper())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set up view models
        vesselViewModel = ViewModelProvider(this, viewModelFactory)
            .get(VesselWatchViewModel::class.java)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<VesselWatchBinding>(
            inflater,
            R.layout.vessel_watch,
            container,
            false)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val settings = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        if (settings != null) {
            showCameras = settings.getBoolean(getString(R.string.user_preference_vessel_watch_cameras), true)
        }

        vesselViewModel.setShowCameras(showCameras)

        dataBinding.vesselViewModel = vesselViewModel

        dataBinding.lifecycleOwner = this

        binding = dataBinding

        initBottomSheets()

        return dataBinding.root
    }


    override fun onMapReady(map: GoogleMap) {

        mMap = map as GoogleMap

        context?.let {
            if (NightModeConfig.nightModeOn(it)) {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    mMap?.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            it, R.raw.map_night_style))

                } catch (e: Resources.NotFoundException) {
                    Log.e("debug", "Can't find style. Error: ", e)
                }

            } else {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    val success: Boolean = mMap?.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            it, R.raw.googlemapstyle
                        )
                    ) == true
                    if (!success) {
                        Log.e("debug", "Style parsing failed.")
                        mMap?.setMapStyle(null)

                    } else {
                        Log.e("debug", "Style parsing failed.")

                    }
                } catch (e: Resources.NotFoundException) {
                    Log.e("debug", "Can't find style. Error: ", e)
                }
            }
        }


        if (requestLocation) {
            checkAppPermissions()
        }

        val settings = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val latitude =
            settings?.getDouble(getString(R.string.user_preference_vessel_watch_latitude), 47.583571)
        val longitude =
            settings?.getDouble(getString(R.string.user_preference_vessel_watch_longitude), -122.473468)
        val zoom = settings?.getFloat(getString(R.string.user_preference_vessel_watch_zoom), 10f)

        val startLocation = latitude?.let { longitude?.let { it1 -> LatLng(it, it1) } }

        startLocation?.let { zoom?.let { it1 -> CameraUpdateFactory.newLatLngZoom(it, it1) } }
            ?.let { mMap?.moveCamera(it) }
        mMap?.setOnMarkerClickListener(this)

        vesselViewModel.vessels.observe(viewLifecycleOwner, Observer { vessels ->
            if (vessels.data != null) {

                // loop over markers, removing any old ones from google map and hash map
                with(vesselMarkers.iterator()) {
                    forEach {
                        it.key.remove()
                        remove()
                    }
                }

                with(vesselLabels.iterator()) {
                    forEach {
                        it.key.remove()
                        remove()
                    }
                }

                with(terminalMarkers.iterator()) {
                    forEach {
                        it.key.remove()
                        remove()
                    }
                }

                for (vessel in vessels.data) {

                    if (vessel.inService) {
                        val vesselMarker = mMap?.addMarker(
                            MarkerOptions()
                                .position(LatLng(vessel.latitude, vessel.longitude))
                                .rotation(vessel.heading.toFloat())
                                .zIndex(3F)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ferry_0))
                        )

                        bitmap.eraseColor(Color.TRANSPARENT)
                        text.color = Color.BLACK
                        text.textSize = 30f
                        text.textAlign = Align.CENTER
                        background.setColor(Color.WHITE)
                        background.style = Paint.Style.FILL
                        canvas.drawPaint(background)
                        canvas.drawText(
                            vesselNames[vessel.vesselName].toString(),
                            (canvas.width / 2).toFloat(),
                            (canvas.height / 2).toFloat() - ((text.descent() + text.ascent()) / 2),
                            text
                        )

                        val label = mMap?.addMarker(
                            MarkerOptions()
                                .anchor(0.5f, 2.0f)
                                .zIndex(3F)
                                .position(LatLng(vessel.latitude, vessel.longitude))
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        )

                        vesselMarker?.let {
                            vesselMarkers[it] = vessel
                        }

                        label?.let {
                            vesselLabels[it] = vessel
                        }

                    }
                }


                val terminals = DistanceUtils.getTerminals()
                for (terminal in terminals) {

                    val terminalMarker = mMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(terminal.first, terminal.second))
                            .zIndex(1F)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.terminal))
                    )

//                    terminalMarker?.let {
//                        terminalMarkers[it] = terminal
//                    }
                }

                if (vessels.status == Status.ERROR) {
                    Toast.makeText(
                        context,
                        getString(R.string.loading_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
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
                    val marker = mMap?.addMarker(MarkerOptions()
                        .position(LatLng(camera.latitude, camera.longitude))
                        .visible(showCameras)
                        .zIndex(2F)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)))
                    marker?.let {
                        cameraMarkers[it] = camera
                    }

                }
            }
        })

        binding.cameraVisibilityFab.setOnClickListener {
            val editor = activity?.let { it1 -> PreferenceManager.getDefaultSharedPreferences(it1).edit() }
            editor?.putBoolean(getString(R.string.user_preference_vessel_watch_cameras), !showCameras)
            editor?.apply()
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

    override fun onResume() {
        super.onResume()
        vesselUpdateHandler.post(vesselUpdateTask)
    }

    override fun onPause() {

        mMap?.let { map ->
            val settings = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            val editor = settings?.edit()
            editor?.putDouble(getString(R.string.user_preference_vessel_watch_latitude), map.projection.visibleRegion.latLngBounds.center.latitude)
            editor?.putDouble(getString(R.string.user_preference_vessel_watch_longitude), map.projection.visibleRegion.latLngBounds.center.longitude)
            editor?.putFloat(getString(R.string.user_preference_vessel_watch_zoom), map.cameraPosition.zoom)
            editor?.apply()
        }

        vesselUpdateHandler.removeCallbacks(vesselUpdateTask)
        super.onPause()
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        vesselMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavVesselDetailsFragment(it.vesselId, "Vessel Watch")
            findNavController().navigate(action)
            return true
        }

        vesselLabels[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavVesselDetailsFragment(it.vesselId, "Vessel Watch")
            findNavController().navigate(action)
            return true
        }

        cameraMarkers[marker]?.let { camera ->

            selectedCameraMarker?.remove()
            val icon = BitmapDescriptorFactory.fromResource(R.drawable.camera_selected)

            selectedCameraMarker = mMap?.addMarker(MarkerOptions()
                .zIndex(2f)
                .position(LatLng(camera .latitude, camera .longitude))
                .visible(true)
                .icon(icon))

            cameraViewModel.setCameraQuery(camera.cameraId)

            binding.includedCameraBottomSheetView.favoriteButton.setOnClickListener {
                cameraViewModel.updateFavorite(camera.cameraId)

                if (this::toast.isInitialized)
                {
                    toast.cancel()
                }

                if (!camera.favorite) {
                    camera.favorite = true
                    toast = Toast.makeText(context, getString(R.string.favorite_added_message), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }
                else {
                    camera.favorite = false
                    toast = Toast.makeText(context, getString(R.string.favorite_removed_message), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }
            }

            BottomSheetBehavior.from(binding.cameraBottomSheet).state =
                BottomSheetBehavior.STATE_EXPANDED

            return true
        }

        return true
    }

    // functions to handle bottom sheet logic
    private fun initBottomSheets() {

        // Camera Bottom Sheet
        cameraViewModel = ViewModelProvider(this, viewModelFactory)
            .get(CameraViewModel::class.java)
        cameraViewModel.setCameraQuery(-1)

        val cameraSheetBehavior = BottomSheetBehavior.from(binding.cameraBottomSheet)
        cameraSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.includedCameraBottomSheetView.cameraViewModel = cameraViewModel

        val behavior = BottomSheetBehavior.from(binding.cameraBottomSheet)

        val bottomSheetBehaviorCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        selectedCameraMarker?.remove()
                    }
                }
            }
        behavior.addBottomSheetCallback(bottomSheetBehaviorCallback)

        binding.includedCameraBottomSheetView.favoriteButton.setOnClickListener(null)
        binding.includedCameraBottomSheetView.favoriteButton.setOnClickListener {
            cameraViewModel.updateFavorite(-1)
        }

        binding.includedCameraBottomSheetView.closeButton.setOnClickListener {
            BottomSheetBehavior.from(binding.cameraBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN
        }

        // if a bottom sheet is open override back button to close sheet
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.let {
                        if (BottomSheetBehavior.from(it.cameraBottomSheet).state == BottomSheetBehavior.STATE_EXPANDED) {
                            BottomSheetBehavior.from(it.cameraBottomSheet).state =
                                BottomSheetBehavior.STATE_HIDDEN
                            return
                        }
                    }
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            })

    }


    // Location Permission
    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun myLocationFine() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        mMap?.isMyLocationEnabled = true
                        requestLocationUpdates()
                    }
                }
        }
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun myLocationCoarse() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        mMap?.isMyLocationEnabled = false
                        requestLocationUpdates()
                    }
                }
        }
    }

    private fun checkAppPermissions() {

        if (Build.VERSION.SDK_INT == 23) {
            myLocationFine()
        } else {

            // Check if app has location permissions granted
            when (PackageManager.PERMISSION_GRANTED) {
                activity?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
                -> {
                    myLocationFine()
                }
                activity?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }
                -> {
                    myLocationCoarse()
                }
                else -> {
                    // Present permission dialog to request permission type
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {

        val locationRequest = LocationRequest()
        locationRequest.numUpdates = 1
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
}
