package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
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
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.VesselWatchBinding
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalAlert
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.common.Status
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraViewModel
import gov.wa.wsdot.android.wsdot.util.NightModeConfig
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.putDouble
import permissions.dispatcher.NeedsPermission
import javax.inject.Inject

class VesselWatchFragment: DaggerFragment(), Injectable, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,SpeedDialView.OnActionSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var vesselViewModel: VesselWatchViewModel
    lateinit var cameraViewModel: CameraViewModel

    var binding by autoCleared<VesselWatchBinding>()

    private var mMap: GoogleMap? = null

    private lateinit var mapFragment: SupportMapFragment

    private val cameraMarkers = HashMap<Marker, Camera>()
    private val terminalMarkers = HashMap<Marker, TerminalAlert>()
    private val vesselMarkers = HashMap<Marker, Vessel>()
    private val vesselMarkerLabels = HashMap<Marker, Vessel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var showCameras: Boolean = true
    private var selectedCameraMarker: Marker? = null
    private var showVessels: Boolean = true
    private var showLabels: Boolean = true
    private var showTerminals: Boolean = true
    private var showTrafficLayer: Boolean = true

    var requestLocation: Boolean = true

    private lateinit var toast: Toast

    // FAB
    private lateinit var mFab: SpeedDialView

    val bitmap = createBitmap(70, 30)
    val canvas = Canvas(bitmap)
    val text = Paint()
    val background = Paint()

    private val vesselNames = mapOf("Cathlamet" to "CAT", "Chelan" to "CHE", "Chetzemoka" to "CHZ", "Chimacum" to "CHI", "Issaquah" to "ISS",
    "Kaleetan" to "KAL", "Kennewick" to "KEN", "Kitsap" to "KIS", "Kittitas" to "KIT", "Puyallup" to "PUY", "Salish" to "SAL", "Samish" to "SAM",
    "Sealth" to "SEA", "Spokane" to "SPO", "Suquamish" to "SUQ", "Tacoma" to "TAC", "Tillikum" to "TIL", "Tokitae" to "TOK", "Walla Walla" to "WAL", "Wenatchee" to "WEN", "Yakima" to "YAK")

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
            showVessels = settings.getBoolean(getString(R.string.user_preference_vessel_watch_vessels), true)
            showLabels = settings.getBoolean(getString(R.string.user_preference_vessel_watch_labels), true)
            showTerminals = settings.getBoolean(getString(R.string.user_preference_vessel_watch_show_terminals), true)
            showTrafficLayer = settings.getBoolean(getString(R.string.user_preference_vessel_watch_show_traffic_layer), true)
        }

        vesselViewModel.setShowCameras(showCameras)
        vesselViewModel.setShowVessels(showVessels)
        vesselViewModel.setShowLabels(showLabels)
        vesselViewModel.setShowTerminals(showTerminals)
        vesselViewModel.setTrafficLayer(showTrafficLayer)

        dataBinding.vesselViewModel = vesselViewModel

        dataBinding.lifecycleOwner = this

        binding = dataBinding

        initBottomSheets()

        initSettingsFAB(dataBinding.root)

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

        if(showTrafficLayer) {
            mMap?.isTrafficEnabled = true
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

                with(vesselMarkerLabels.iterator()) {
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
                                    .visible(showVessels)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ferry_0))
                            )

                            bitmap.eraseColor(Color.TRANSPARENT)
                            text.color = Color.BLACK
                            text.textSize = 35F
                            text.textAlign = Align.CENTER
                            background.setColor(Color.WHITE)
                            background.style = Paint.Style.FILL
                            canvas.drawPaint(background)

                            var vesselName =  vesselNames[vessel.vesselName].toString()

                            if (vesselName == "null") {
                                vesselName = ""
                            }

                            canvas.drawText(
                                vesselName,
                                (canvas.width/2).toFloat(),
                                (canvas.height/2).toFloat() - ((text.descent() + text.ascent())/2),
                                text
                            )

                            val (u,v) = checkHeading(vessel.heading.toFloat())

                            val label = mMap?.addMarker(
                                MarkerOptions()
                                    .anchor(u,v)
                                    .zIndex(3F)
                                    .visible(showLabels)
                                    .position(LatLng(vessel.latitude, vessel.longitude))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            )

                            vesselMarker?.let {
                                vesselMarkers[it] = vessel
                            }

                            label?.let {
                                vesselMarkerLabels[it] = vessel
                            }

                        }
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
                        .zIndex(1F)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)))
                    marker?.let {
                        cameraMarkers[it] = camera
                    }

                }

            }
        })

        vesselViewModel.terminals.observe(viewLifecycleOwner, Observer { terminals ->

            if (terminals.data != null) {

                with(terminalMarkers.iterator()) {
                    forEach {
                        it.key.remove()
                        remove()
                    }
                }

                for (terminal in terminals.data) {
                    val marker = mMap?.addMarker(MarkerOptions()
                        .position(LatLng(terminal.latitude, terminal.longitude))
                        .visible(showTerminals)
                        .zIndex(2F)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.terminal)))
                    marker?.let {
                        terminalMarkers[it] = terminal
                    }
                }
            }
        })
    }

    fun checkHeading(heading: Float): Pair<Float,Float> {
        return when (heading) {
            in 0F..15F -> Pair(0.5F, -0.5F)
            in 16F..30F -> Pair(0.5F, -1.0F)
            in 31F..45F -> Pair(0.0F, -1.0F)
            in 46F..60F -> Pair(0.0F, -1.0F)
            in 61F..75F -> Pair(0.0F, -1.0F)
            in 76F..90F -> Pair(0.0F, -1.0F)
            in 91F..105F -> Pair(0.0F, -1.5F)
            in 106F..120F -> Pair(0.0F, -2.0F)
            in 121F..145F -> Pair(0.0F, -2.0F)
            in 146F..160F -> Pair(0.0F, -2.5F)
            in 161F..175F -> Pair(0.5F, -2.5F)
            in 176F..190F -> Pair(0.5F, -2.5F)
            in 191F..205F -> Pair(0.5F, -2.5F)
            in 206F..220F -> Pair(1.0F, -2.0F)
            in 221F..245F -> Pair(1.0F, -2.0F)
            in 246F..260F -> Pair(1.0F, -1.5F)
            in 261F..275F -> Pair(1.0F, -1.0F)
            in 276F..290F -> Pair(1.0F, -1.0F)
            in 291F..305F -> Pair(1.0F, -1.0F)
            in 306F..320F -> Pair(1.0F, -1.0F)
            in 321F..345F -> Pair(0.5F, -1.0F)
            in 346F..360F -> Pair(0.5F, -0.5F)
            else -> Pair(0.0F, -1.0F)
        }
    }

    private fun setTrafficLayerVisibility(visibility: Boolean){
        mMap?.isTrafficEnabled = visibility
    }


    private fun setCameraMarkerVisibility(visibility: Boolean){
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

    private fun setVesselMarkerVisibility(visibility: Boolean){
        val editor = activity?.let { it1 -> PreferenceManager.getDefaultSharedPreferences(it1).edit() }
        editor?.putBoolean(getString(R.string.user_preference_vessel_watch_vessels), !showVessels)
        editor?.apply()
        showVessels = !showVessels
        vesselViewModel.setShowVessels(showVessels)

        // loop over markers, setting viability
        with(vesselMarkers.iterator()) {
            forEach {
                it.key.isVisible = showVessels
            }
        }
    }

    private fun setLabelMarkerVisibility(visibility: Boolean){
        val editor = activity?.let { it1 -> PreferenceManager.getDefaultSharedPreferences(it1).edit() }
        editor?.putBoolean(getString(R.string.user_preference_vessel_watch_labels), !showLabels)
        editor?.apply()
        showLabels = !showLabels
        vesselViewModel.setShowLabels(showLabels)

        // loop over labels, setting viability
        with(vesselMarkerLabels.iterator()) {
            forEach {
                it.key.isVisible = showLabels
            }
        }
    }

    private fun setTerminalLayerVisibility(visibility: Boolean){
        val editor = activity?.let { it1 -> PreferenceManager.getDefaultSharedPreferences(it1).edit() }
        editor?.putBoolean(getString(R.string.user_preference_vessel_watch_show_terminals), !showTerminals)
        editor?.apply()
        showTerminals = !showTerminals
        vesselViewModel.setShowTerminals(showTerminals)
        // loop over camera markers, setting viability
        with(terminalMarkers.iterator()) {
            forEach {
                it.key.isVisible = showTerminals
            }
        }
    }





    // settings FAB

    private fun initSettingsFAB(view: View) {

        mFab = view.findViewById(R.id.speedDial)

        mFab.addActionItem(getVesselLabelMarkerVisibility(), 0)
        mFab.addActionItem(getVesselMarkerVisibility(), 1)
        mFab.addActionItem(getCameraMarkerVisibilityAction(), 2)
        mFab.addActionItem(getTerminalMarkerVisibilityAction(), 3)
        mFab.addActionItem(getTrafficLayerVisibilityAction(), 4)
        mFab.mainFab.imageTintList = ColorStateList.valueOf(Color.WHITE)
        mFab.setOnActionSelectedListener(this)

    }

    private fun getTrafficLayerVisibilityAction(): SpeedDialActionItem  {

        val settings = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val showTrafficLayer = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_show_traffic_layer), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showTrafficLayer!!) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_traffic_layer_visibility_action, icon)
            .setLabel(R.string.fab_traffic_layer_label)
            .setLabelColor(resources.getColor(R.color.cardLightGray))
            .setLabelBackgroundColor(Color.WHITE)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getTerminalMarkerVisibilityAction(): SpeedDialActionItem  {

        val settings = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val showTerminalLayer = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_show_terminals), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showTerminalLayer!!) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_terminal_visibility_action, icon)
            .setLabel(R.string.fab_terminal_layer_label)
            .setLabelColor(resources.getColor(R.color.cardLightGray))
            .setLabelBackgroundColor(Color.WHITE)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getCameraMarkerVisibilityAction(): SpeedDialActionItem {

        val settings = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val showCameras =
            settings?.getBoolean(getString(R.string.user_preference_vessel_watch_cameras), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showCameras!!) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_vessel_camera_visibility_action, icon)
            .setLabel(R.string.fab_camera_label)
            .setLabelColor(resources.getColor(R.color.cardLightGray))
            .setLabelBackgroundColor(Color.WHITE)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getVesselMarkerVisibility(): SpeedDialActionItem {

        val settings = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val showVessels =
            settings?.getBoolean(getString(R.string.user_preference_vessel_watch_vessels), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showVessels!!) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_vessel_layer_visibility_action, icon)
            .setLabel(R.string.fab_vessel_label)
            .setLabelColor(resources.getColor(R.color.cardLightGray))
            .setLabelBackgroundColor(Color.WHITE)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getVesselLabelMarkerVisibility(): SpeedDialActionItem {

        val settings = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val showLabels =
            settings?.getBoolean(getString(R.string.user_preference_vessel_watch_labels), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showLabels!!) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_vessel_label_visibility_action, icon)
            .setLabel(R.string.fab_label_label)
            .setLabelColor(resources.getColor(R.color.cardLightGray))
            .setLabelBackgroundColor(Color.WHITE)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem?.let {
            when(it.id) {

                R.id.fab_vessel_camera_visibility_action -> {
                    val settings = context?.let { it1 ->
                        PreferenceManager.getDefaultSharedPreferences(
                            it1
                        )
                    }
                    val show = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_cameras), true)
                    val editor = settings?.edit()
                    editor?.putBoolean(getString(R.string.user_preference_vessel_watch_cameras), !show!!)
                    editor?.apply()

                    setCameraMarkerVisibility(!show!!)

                    mFab.replaceActionItem(actionItem, getCameraMarkerVisibilityAction())

                }

                R.id.fab_traffic_layer_visibility_action -> {
                    val settings = context?.let { it1 ->
                        PreferenceManager.getDefaultSharedPreferences(
                            it1
                        )
                    }
                    val show = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_show_traffic_layer), true)
                    val editor = settings?.edit()
                    editor?.putBoolean(getString(R.string.user_preference_vessel_watch_show_traffic_layer), !show!!)
                    editor?.apply()

                    setTrafficLayerVisibility(!show!!)
                    showTrafficLayer = !show

                    mFab.replaceActionItem(actionItem, getTrafficLayerVisibilityAction())

                }

                R.id.fab_terminal_visibility_action -> {
                    val settings = context?.let { it1 ->
                        PreferenceManager.getDefaultSharedPreferences(
                            it1
                        )
                    }
                    val show = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_show_terminals), true)
                    val editor = settings?.edit()
                    editor?.putBoolean(getString(R.string.user_preference_vessel_watch_show_terminals), !show!!)
                    editor?.apply()

                    setTerminalLayerVisibility(!show!!)
                    showTerminals = !show

                    mFab.replaceActionItem(actionItem, getTerminalMarkerVisibilityAction())

                }

                R.id.fab_vessel_layer_visibility_action -> {
                    val settings = context?.let { it1 ->
                        PreferenceManager.getDefaultSharedPreferences(
                            it1
                        )
                    }
                    val show = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_vessels), true)
                    val editor = settings?.edit()
                    editor?.putBoolean(getString(R.string.user_preference_vessel_watch_vessels), !show!!)
                    editor?.apply()

                    setVesselMarkerVisibility(!show!!)
                    showVessels = !show

                    mFab.replaceActionItem(actionItem, getVesselMarkerVisibility())

                }

                R.id.fab_vessel_label_visibility_action -> {
                    val settings = context?.let { it1 ->
                        PreferenceManager.getDefaultSharedPreferences(
                            it1
                        )
                    }
                    val show = settings?.getBoolean(getString(R.string.user_preference_vessel_watch_labels), true)
                    val editor = settings?.edit()
                    editor?.putBoolean(getString(R.string.user_preference_vessel_watch_labels), !show!!)
                    editor?.apply()

                    setLabelMarkerVisibility(!show!!)
                    showLabels = !show

                    mFab.replaceActionItem(actionItem, getVesselLabelMarkerVisibility())

                }

                else -> return true
            }
        }
        return true
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

        terminalMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavTerminalFragment(it.terminalID, "Terminal Bulletins")
            findNavController().navigate(action)
            return true
        }

        vesselMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavVesselDetailsFragment(it.vesselId, "Vessel Watch")
            findNavController().navigate(action)
            return true
        }

        vesselMarkerLabels[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavVesselDetailsFragment(it.vesselId, "Vessel Watch")
            findNavController().navigate(action)
            return true
        }

        cameraMarkers[marker]?.let { camera ->

            selectedCameraMarker?.remove()
            val icon = BitmapDescriptorFactory.fromResource(R.drawable.camera_selected)

            selectedCameraMarker = mMap?.addMarker(MarkerOptions()
                .zIndex(1f)
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
