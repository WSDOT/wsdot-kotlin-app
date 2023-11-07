package gov.wa.wsdot.android.wsdot.ui.trafficmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.maps.android.MarkerManager
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MapFragmentBinding
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.model.MountainPassItem
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.MapLocationItem
import gov.wa.wsdot.android.wsdot.model.RestAreaItem
import gov.wa.wsdot.android.wsdot.model.map.CameraClusterItem
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraViewModel
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertViewModel
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.MountainPassViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.favoriteLocation.FavoriteLocationViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.restareas.RestAreaViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts.MapHighwayAlertsViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts.TravelChartsViewModel
import gov.wa.wsdot.android.wsdot.util.*
import gov.wa.wsdot.android.wsdot.util.map.CameraClusterManager
import gov.wa.wsdot.android.wsdot.util.map.CameraRenderer
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*
import javax.inject.Inject


class TrafficMapFragment : DaggerFragment(), Injectable, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    ClusterManager.OnClusterItemClickListener<CameraClusterItem>,
    ClusterManager.OnClusterClickListener<CameraClusterItem>,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
    SpeedDialView.OnActionSelectedListener, Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var mapHighwayAlertsViewModel: MapHighwayAlertsViewModel
    lateinit var mapCamerasViewModel: MapCamerasViewModel
    lateinit var cameraViewModel: CameraViewModel
    lateinit var highwayAlertViewModel: HighwayAlertViewModel
    lateinit var restAreaViewModel: RestAreaViewModel
    lateinit var favoriteLocationViewModel: FavoriteLocationViewModel
    lateinit var travelChartsViewModel: TravelChartsViewModel
    lateinit var mapLocationViewModel: MapLocationViewModel
    lateinit var mountainPassViewModel: MountainPassViewModel


    // Maps markers to their underlying data
    private val highwayAlertMarkers = HashMap<Marker, HighwayAlert>()
    private val restAreaMarkers = HashMap<Marker, RestAreaItem>()
    private val cameraClusterItems = mutableListOf<CameraClusterItem>()
    private val mountainPassMarkers = HashMap<Marker, MountainPass>()

    private var selectedCameraMarker: Marker? = null
    private var selectedAlertMarker: Marker? = null

    var showAlerts: Boolean = true
    var showRestAreas: Boolean = true
    var showMountainPasses: Boolean = true
    var requestLocationUpgrade: Boolean = true
    var goToLocation: Boolean = true
    private var alertQueryTask: Boolean = false

    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Clusters
    private lateinit var mCameraClusterManager: CameraClusterManager
    private lateinit var mMarkerManager: MarkerManager

    var binding by nullableAutoCleared<MapFragmentBinding>()

    // Camera update task timer
    var t: Timer? = null

    // Approximate location radius circle
    private var radiusCircle: Circle? = null

    // Current location cancellation token
    private var cancellationTokenSource = CancellationTokenSource()

    // FAB
    private lateinit var mFab: SpeedDialView

    // Toast
    private lateinit var toast: Toast

    // Update Tasks
    private lateinit var mapUpdateHandler: Handler
    private val alertsUpdateTask = object: Runnable {
        override fun run() {

            if (alertQueryTask) {
                mapHighwayAlertsViewModel.setAlertQuery(
                    mMap.projection.visibleRegion.latLngBounds,
                    false
                )
            }
                mapHighwayAlertsViewModel.refresh()
            mapUpdateHandler.postDelayed(this, 300000)
            alertQueryTask = true

        }
    }

    // Determine which permissions have been granted
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

            } else -> {
            println("No location access granted.")
        }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mapUpdateHandler = Handler(Looper.getMainLooper())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

//        val adTargets = mapOf("wsdotapp" to resources.getString(R.string.ad_target_traffic))
//        (activity as MainActivity).enableAds(adTargets)
        
        (activity as MainActivity).disableAds()

        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        // Inflate the layout for this fragment

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<MapFragmentBinding>(
            inflater,
            R.layout.map_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner

        initBottomBar(dataBinding.root)

        // get it from the Activity so it can be shared with the Map Alerts Fragment
        mapHighwayAlertsViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MapHighwayAlertsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        mapLocationViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MapLocationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        mapCamerasViewModel = ViewModelProvider(this, viewModelFactory)
            .get(MapCamerasViewModel::class.java)

        restAreaViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RestAreaViewModel::class.java)

        favoriteLocationViewModel = ViewModelProvider(this, viewModelFactory)
            .get(FavoriteLocationViewModel::class.java)

        travelChartsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TravelChartsViewModel::class.java)

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        showAlerts = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_highway_alerts), true)
        showRestAreas = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_rest_areas), true)
        showMountainPasses = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_mountain_passes), true)

        mountainPassViewModel = ViewModelProvider(this, viewModelFactory)
            .get(MountainPassViewModel::class.java)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initSettingsFAB(dataBinding.root)

        // Check for travel charts
        travelChartsViewModel.travelCharts.observe(viewLifecycleOwner, Observer { chartsResource ->
            chartsResource.data?.let {
                if (it.available) {
                    val bottomAppBar = dataBinding.root.findViewById<BottomAppBar>(R.id.bottom_app_bar)
                    bottomAppBar.menu.setGroupVisible(R.id.travel_charts_group, true)
                    bottomAppBar.menu.findItem(R.id.action_travel_charts).icon = BadgeDrawable.getMenuBadge(requireContext(), R.drawable.ic_menu_travel_charts, "!")
                }
            }
        })

        mapLocationViewModel.mapLocation.observe(viewLifecycleOwner, Observer {location ->
            if (::mMap.isInitialized) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.location, location.zoom))
            }
        })

        binding = dataBinding

        initBottomSheets()

        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()
        mapUpdateHandler.post(alertsUpdateTask)
    }

    override fun onPause() {
        super.onPause()

        from(binding!!.cameraBottomSheet).state = STATE_HIDDEN
        from(binding!!.highwayAlertBottomSheet).state = STATE_HIDDEN

        if (::mMap.isInitialized) {

            val location = MapLocationItem(
                LatLng(
                    mMap.projection.visibleRegion.latLngBounds.center.latitude,
                    mMap.projection.visibleRegion.latLngBounds.center.longitude
                ),
                mMap.cameraPosition.zoom
            )

            mapLocationViewModel.updateLocation(location)

        }

        mapUpdateHandler.removeCallbacks(alertsUpdateTask)

        t?.cancel()
        alertQueryTask = false

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.traffic_map_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Check location permissions when menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_my_location -> {
                goToLocation = true
                if (requestLocationUpgrade) {
                    myLocationFine()
                }
                checkAppPermissions()
            }
        }

        return false

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
                } catch (e: NotFoundException) {
                    Log.e("debug", "Can't find style. Error: ", e)
                }
            }
        }

        mMap.clear()
        goToLocation = false
        checkAppPermissions()

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false

        mMap.isTrafficEnabled = true

        mapLocationViewModel.mapLocation.removeObservers(viewLifecycleOwner)
        mapLocationViewModel.mapLocation.observe(viewLifecycleOwner, Observer {location ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.location, location.zoom))
        })

        // init a marker manager that will handle clusters and regular map markers
        mMarkerManager = MarkerManager(mMap)

        // set up a cluster manager with the MarkerManager
        mCameraClusterManager = CameraClusterManager(context, mMap, mMarkerManager)
        // Give it a custom renderer (used to modify icons, etc...)
        mCameraClusterManager.renderer = CameraRenderer(context, mMap, mCameraClusterManager)
        mCameraClusterManager.renderer.setAnimation(false)

        mCameraClusterManager.setOnClusterItemClickListener(this)
        mCameraClusterManager.setOnClusterClickListener(this)

        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnMarkerClickListener(mMarkerManager)

        // init collection for selected camera
        mMarkerManager.newCollection(getString(R.string.selected_marker_collection_id))
        mMarkerManager.getCollection(getString(R.string.selected_marker_collection_id)).setOnMarkerClickListener(this)

        // init a new collection for alert markers
        mMarkerManager.newCollection(getString(R.string.highway_alert_marker_collection_id))
        mMarkerManager.getCollection(getString(R.string.highway_alert_marker_collection_id)).setOnMarkerClickListener(this)

        // init a new collection for alert markers
        mMarkerManager.newCollection(getString(R.string.mountain_pass_marker_collection_id))
        mMarkerManager.getCollection(getString(R.string.mountain_pass_marker_collection_id)).setOnMarkerClickListener(this)

        mapHighwayAlertsViewModel.alerts.observe(viewLifecycleOwner, Observer { alerts ->
            if (alerts.data != null) {

                with(highwayAlertMarkers.iterator()) {
                    forEach {
                        mMarkerManager.getCollection(getString(R.string.highway_alert_marker_collection_id)).remove(it.key)
                        it.key.remove()
                        remove()
                    }
                }

                for (alert in alerts.data) {

                    // don't show alerts with coordinates (0,0)
                    if (alert.startLatitude == 0.0
                        && alert.startLongitude == 0.0) {
                        continue
                    }

                    val alertIcon: BitmapDescriptor = when(alert.travelCenterPriorityId) {
                        4 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                        3 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                        2 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_high)
                        1 -> BitmapDescriptorFactory.fromResource(R.drawable.closed)
                        else -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                    }

                    val marker = mMarkerManager.getCollection(getString(R.string.highway_alert_marker_collection_id)).addMarker(
                        MarkerOptions()
                            .position(LatLng(alert.startLatitude, alert.startLongitude))
                            .visible(showAlerts)
                            .icon(alertIcon))

                    highwayAlertMarkers[marker] = alert
                }
            }
        })


        mapCamerasViewModel.cameras.observe(viewLifecycleOwner, Observer { cameras ->
            if (cameras.data != null) {

                cameraClusterItems.clear()
                mCameraClusterManager.clearItems()

                for (camera in cameras.data) {
                    val clusterItem = CameraClusterItem(camera.latitude, camera.longitude, camera)
                    mCameraClusterManager.addItem(clusterItem)
                    cameraClusterItems.add(clusterItem)
                }

                mCameraClusterManager.cluster()
            }
        })


        mountainPassViewModel.passes.observe(viewLifecycleOwner, Observer { passes ->
            if (passes.data != null) {
                for (pass in passes.data) {
                    var icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mountainpass)
                    val marker = mMarkerManager.getCollection(getString(R.string.mountain_pass_marker_collection_id)).addMarker(
                        MarkerOptions()
                            .position(LatLng(pass.latitude, pass.longitude))
                            .visible(showMountainPasses)
                            .icon(icon))
                    mountainPassMarkers[marker] = pass
                }
            }
        })


        // init a new collection for rest area markers
        mMarkerManager.newCollection(getString(R.string.rest_area_marker_collection_id))
        mMarkerManager.getCollection(getString(R.string.rest_area_marker_collection_id)).setOnMarkerClickListener(this)

        val restAreaJson = resources.openRawResource(R.raw.restareas).bufferedReader().use { it.readText() }
        restAreaViewModel.setRestAreaData(restAreaJson)

        restAreaViewModel.restAreas.observe(viewLifecycleOwner, Observer { restAreas ->

            with(restAreaMarkers.iterator()) {
                forEach {
                    mMarkerManager.getCollection(getString(R.string.rest_area_marker_collection_id)).remove(it.key)
                    it.key.remove()
                    remove()
                }
            }

            for (restArea in restAreas) {

                val marker = mMarkerManager.getCollection(getString(R.string.rest_area_marker_collection_id)).addMarker(
                    MarkerOptions()
                        .position(LatLng(restArea.latitude, restArea.longitude))
                        .visible(showRestAreas)
                        .icon(BitmapDescriptorFactory.fromResource(restArea.icon)))

                restAreaMarkers[marker] = restArea

            }
        })

    }

    override fun onCameraMoveStarted(p0: Int) {
        mFab.close()
    }

    override fun onCameraIdle() {
        mapCamerasViewModel.setCameraQuery(mMap.projection.visibleRegion.latLngBounds, false)
        mapHighwayAlertsViewModel.setAlertQuery(mMap.projection.visibleRegion.latLngBounds, false)
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        highwayAlertMarkers[marker]?.let { alert ->

            selectedAlertMarker?.remove()

            val icon: BitmapDescriptor = when(alert.travelCenterPriorityId) {
                4 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
                3 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_moderate)
                2 -> BitmapDescriptorFactory.fromResource(R.drawable.alert_high)
                1 -> BitmapDescriptorFactory.fromResource(R.drawable.closed)
                else -> BitmapDescriptorFactory.fromResource(R.drawable.alert_low)
            }

            selectedAlertMarker = mMarkerManager.getCollection(getString(R.string.selected_marker_collection_id))
                .addMarker(MarkerOptions()
                    .zIndex(100f)
                    .position(LatLng(alert .startLatitude, alert .startLongitude))
                    .visible(true)
                    .icon(icon))

            from(binding!!.cameraBottomSheet).state = STATE_HIDDEN

            highwayAlertViewModel.setAlertQuery(alert .alertId)
            binding!!.highwayAlertBottomSheet.requestLayout()
            from(binding!!.highwayAlertBottomSheet).state = STATE_EXPANDED

            return true
        }

        restAreaMarkers[marker]?.let {
            if (findNavController().currentDestination?.id != R.id.navRestAreaFragment) {
                val action = NavGraphDirections.actionGlobalNavRestAreaFragment(it.description)
                findNavController().navigate(action)
            }
            return true
        }

        mountainPassMarkers[marker]?.let {
            if (findNavController().currentDestination?.id != R.id.navMountainPassReportFragment) {
                val action = NavGraphDirections.actionGlobalNavMountainPassReportFragment(it.passId)
                findNavController().navigate(action)
                (activity as MainActivity).supportActionBar?.title = it.passName
            }
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

        val cameraSheetBehavior = from(binding!!.cameraBottomSheet)
        cameraSheetBehavior.state = STATE_HIDDEN

        val cameraBottomSheetBehaviorCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when(newState) {
                        STATE_EXPANDED -> {
                            from(binding!!.highwayAlertBottomSheet).state = STATE_HIDDEN
                            t?.cancel()
                            t = Timer()
                            t?.scheduleAtFixedRate(
                                object : TimerTask() {
                                    override fun run() {
                                        appExecutors.mainThread().execute {
                                            binding?.invalidateAll()
                                        }
                                    }
                                },
                                60000,
                                300000
                            )
                        }
                        STATE_HIDDEN -> {
                            selectedCameraMarker?.remove()
                            t?.cancel()
                            appExecutors.mainThread().execute {
                                binding?.invalidateAll()
                            }
                        }
                        STATE_DRAGGING -> {}
                        STATE_HALF_EXPANDED -> {}
                        STATE_SETTLING -> {}
                        STATE_COLLAPSED -> {
                            selectedCameraMarker?.remove()
                            t?.cancel()
                        }
                    }
                }
            }

        cameraSheetBehavior.addBottomSheetCallback(cameraBottomSheetBehaviorCallback)

        binding!!.includedCameraBottomSheetView.favoriteButton.setOnClickListener(null)
        binding!!.includedCameraBottomSheetView.favoriteButton.setOnClickListener {
            cameraViewModel.updateFavorite(-1)
        }

        binding!!.includedCameraBottomSheetView.closeButton.setOnClickListener {
            from(binding!!.cameraBottomSheet).state = STATE_HIDDEN
        }

        binding!!.includedCameraBottomSheetView.cameraViewModel = cameraViewModel

        // highway alert view model
        highwayAlertViewModel = ViewModelProvider(this, viewModelFactory)
            .get(HighwayAlertViewModel::class.java)
        highwayAlertViewModel.setAlertQuery(-1)

        val alertSheetBehavior = from(binding!!.highwayAlertBottomSheet)
        alertSheetBehavior.state = STATE_HIDDEN

        val alertBottomSheetBehaviorCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when(newState) {
                        STATE_EXPANDED -> { }
                        STATE_COLLAPSED -> {
                            selectedAlertMarker?.remove()
                        }
                        STATE_DRAGGING -> {}
                        STATE_HALF_EXPANDED -> {}
                        STATE_HIDDEN -> {
                            selectedAlertMarker?.remove()
                        }
                        STATE_SETTLING -> {}
                    }
                }
            }

        alertSheetBehavior.addBottomSheetCallback(alertBottomSheetBehaviorCallback)

        binding!!.includedHighwayAlertBottomSheet.closeButton.setOnClickListener {
            from(binding!!.highwayAlertBottomSheet).state = STATE_HIDDEN
        }

        binding!!.includedHighwayAlertBottomSheet.viewModel = highwayAlertViewModel

        // if a bottom sheet is open override back button to close sheet

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding?.let {
                    if (from(it.highwayAlertBottomSheet).state == STATE_EXPANDED) {
                        from(it.highwayAlertBottomSheet).state = STATE_HIDDEN
                        return
                    }
                    else if (from(it.cameraBottomSheet).state == STATE_EXPANDED) {
                        from(it.cameraBottomSheet).state = STATE_HIDDEN
                        return
                    }
                }
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

    }

    // functions to manage non-clustered marker collections

    private fun setHighwayAlertMarkerVisibility(visibility: Boolean){
        val collection = mMarkerManager.getCollection(getString(R.string.highway_alert_marker_collection_id))
        for (marker in collection.markers) {
            marker.isVisible = visibility
        }
    }

    private fun setRestAreaMarkerVisibility(visibility: Boolean){
        val collection = mMarkerManager.getCollection(getString(R.string.rest_area_marker_collection_id))
        for (marker in collection.markers) {
            marker.isVisible = visibility
        }

    }

    private fun setMountainPassMarkerVisibility(visibility: Boolean){
        val collection = mMarkerManager.getCollection(getString(R.string.mountain_pass_marker_collection_id))
        for (marker in collection.markers) {
            marker.isVisible = visibility
        }

    }


    // Google Maps Cluster Utils

    private fun setCameraMarkerVisibility(visibility: Boolean){
        mCameraClusterManager.markerCollection?.markers?.let{ markers ->
            for (marker in markers){
                marker.isVisible = visibility
            }
        }
        mCameraClusterManager.clusterMarkerCollection?.markers?.let { clusters ->
            for (cluster in clusters){
                cluster.isVisible = visibility
            }
        }
    }

    // Called when a clusterable marker is clicked
    override fun onClusterItemClick(p0: CameraClusterItem?): Boolean {
        p0?.let { cameraClusterItem ->

            from(binding!!.highwayAlertBottomSheet).state = STATE_HIDDEN

            selectedCameraMarker?.remove()
            val icon = BitmapDescriptorFactory.fromResource(R.drawable.camera_selected)

            selectedCameraMarker = mMarkerManager.getCollection(getString(R.string.selected_marker_collection_id))
                .addMarker(MarkerOptions()
                .zIndex(100f)
                .position(LatLng(cameraClusterItem.mCamera.latitude, cameraClusterItem.mCamera.longitude))
                .visible(true)
                .icon(icon))

            cameraViewModel.setCameraQuery(cameraClusterItem.mCamera.cameraId)

            binding!!.includedCameraBottomSheetView.favoriteButton.setOnClickListener {
                cameraViewModel.updateFavorite(cameraClusterItem.mCamera.cameraId)
            }

            from(binding!!.cameraBottomSheet).state = STATE_EXPANDED
        }
        return true
    }

    // Called when a cluster that represents multiple markers is clicked
    override fun onClusterClick(p0: Cluster<CameraClusterItem>?): Boolean {

        p0?.let { clusterItems ->

            // Open if cluster has 10 or less items?
            if (clusterItems.size <= 10) {

                val locations = clusterItems.items.map { LatLng(it.mCamera.latitude, it.mCamera.longitude)  }.toTypedArray()
                val latitude = locations[0].latitude
                val longitude = locations[0].longitude

                if  (locations.any{ it.latitude != latitude && it.longitude != longitude }) {

                    val builder = LatLngBounds.builder()
                    for (item in clusterItems.items) {
                        builder.include(item.position)
                    }

                    val bounds = builder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                } else {
                    if (findNavController().currentDestination?.id != R.id.navCameraListFragment) {
                        val action = NavGraphDirections.actionGlobalNavCameraListFragment(
                            clusterItems.items.map { it.mCamera.cameraId }.toIntArray(),
                            "Traffic Cameras"
                        )
                        findNavController().navigate(action)
                    }
                }

            } else {
                val builder = LatLngBounds.builder()
                for (item in clusterItems.items) {
                    builder.include(item.position)
                }

                val bounds = builder.build()
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
        return true
    }

    // settings FAB

    private fun initSettingsFAB(view: View) {

        mFab = view.findViewById(R.id.speedDial)

        mFab.addActionItem(getCameraClusterAction(), 0)
        mFab.addActionItem(getCameraVisibilityAction(), 1)
        mFab.addActionItem(getHighwayAlertsVisibilityAction(), 2)
        mFab.addActionItem(getRestAreasVisibilityAction(), 3)
        mFab.addActionItem(getMountainPassVisibilityAction(), 4)

        mFab.mainFab.imageTintList = ColorStateList.valueOf(Color.WHITE)

        mFab.setOnActionSelectedListener(this)

    }

    private fun getHighwayAlertsVisibilityAction(): SpeedDialActionItem  {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_highway_alerts), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_highway_alert_visibility_action, icon)
            .setLabel(R.string.fab_highway_alerts_label)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getRestAreasVisibilityAction(): SpeedDialActionItem  {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showRestAreas = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_rest_areas), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showRestAreas) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_rest_area_visibility_action, icon)
            .setLabel(R.string.fab_rest_areas_label)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getMountainPassVisibilityAction(): SpeedDialActionItem  {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showMountainPasses = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_mountain_passes), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showMountainPasses) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_mountain_passes_visibility_action, icon)
            .setLabel(R.string.fab_mountain_passes_label)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getCameraVisibilityAction(): SpeedDialActionItem {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_cameras), true)

        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue: TypedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_layers

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_camera_visibility_action, icon)
            .setLabel(R.string.fab_camera_label)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getCameraClusterAction(): SpeedDialActionItem {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_cluster_cameras), true)
        var actionColor = resources.getColor(R.color.wsdotGreen)

        activity?.let {
            val typedValue = TypedValue()
            it.theme.resolveAttribute(R.attr.themeColorAccent, typedValue, true)
            actionColor = typedValue.data
        }

        var icon = R.drawable.ic_done

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
        }

        return SpeedDialActionItem.Builder(R.id.fab_camera_cluster_action, icon)
            .setLabel(R.string.fab_camera_Clusters_label)
            .setFabImageTintColor(Color.WHITE)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {

        actionItem?.let {
            when(it.id) {
                R.id.fab_camera_visibility_action -> {
                    val settings = PreferenceManager.getDefaultSharedPreferences(context)
                    val show = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_cameras), true)
                    val editor = settings.edit()
                    editor.putBoolean(getString(R.string.user_preference_traffic_map_show_cameras), !show)
                    editor.apply()

                    setCameraMarkerVisibility(!show)

                    mFab.replaceActionItem(actionItem, getCameraVisibilityAction())

                }

                R.id.fab_rest_area_visibility_action -> {
                    val settings = PreferenceManager.getDefaultSharedPreferences(context)
                    val show = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_rest_areas), true)
                    val editor = settings.edit()
                    editor.putBoolean(getString(R.string.user_preference_traffic_map_show_rest_areas), !show)
                    editor.apply()

                    setRestAreaMarkerVisibility(!show)
                    showRestAreas = !show

                    mFab.replaceActionItem(actionItem, getRestAreasVisibilityAction())
                }

                R.id.fab_mountain_passes_visibility_action -> {
                    val settings = PreferenceManager.getDefaultSharedPreferences(context)
                    val show = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_mountain_passes), true)
                    val editor = settings.edit()
                    editor.putBoolean(getString(R.string.user_preference_traffic_map_show_mountain_passes), !show)
                    editor.apply()

                    setMountainPassMarkerVisibility(!show)
                    showMountainPasses = !show

                    mFab.replaceActionItem(actionItem, getMountainPassVisibilityAction())
                }

                R.id.fab_highway_alert_visibility_action -> {
                    val settings = PreferenceManager.getDefaultSharedPreferences(context)
                    val show = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_highway_alerts), true)
                    val editor = settings.edit()
                    editor.putBoolean(getString(R.string.user_preference_traffic_map_show_highway_alerts), !show)
                    editor.apply()

                    setHighwayAlertMarkerVisibility(!show)
                    showAlerts = !show

                    mFab.replaceActionItem(actionItem, getHighwayAlertsVisibilityAction())

                }

                R.id.fab_camera_cluster_action -> {
                    val settings = PreferenceManager.getDefaultSharedPreferences(context)
                    val cluster = settings.getBoolean(getString(R.string.user_preference_traffic_map_cluster_cameras), true)
                    val editor = settings.edit()
                    editor.putBoolean(getString(R.string.user_preference_traffic_map_cluster_cameras), !cluster)
                    editor.commit()

                    mCameraClusterManager.clearItems()
                    mCameraClusterManager.addItems(cameraClusterItems)
                    mCameraClusterManager.cluster()

                    mFab.replaceActionItem(actionItem, getCameraClusterAction())

                }
                else -> return true
            }
        }
        return true
    }

    // bottom bar setup
    private fun initBottomBar(view: View){
        val bottomBar = view.findViewById<BottomAppBar>(R.id.bottom_app_bar)
        bottomBar.replaceMenu(R.menu.traffic_map_bottom_appbar_menu)
        bottomBar.setOnMenuItemClickListener(this)
        bottomBar.elevation = 0F
        val filler = view.findViewById<BottomAppBar>(R.id.filler)
        filler.elevation = 0F
    }

    // For bottom menu
    override fun onMenuItemClick(item: MenuItem?): Boolean {

        item?.let {
            when(it.itemId) {
                R.id.action_refresh -> {

                    mapHighwayAlertsViewModel.setAlertQuery(mMap.projection.visibleRegion.latLngBounds, false)
                    mapCamerasViewModel.setCameraQuery(mMap.projection.visibleRegion.latLngBounds, false)
                    mapHighwayAlertsViewModel.refresh()
                    mapCamerasViewModel.refresh()

                    if (this::toast.isInitialized)
                    {
                        toast.cancel()
                    }

                    toast = Toast.makeText(context, "refreshing...", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }

                R.id.action_go_to_location -> {
                    if (findNavController().currentDestination?.id != R.id.navGoToLocationBottomSheetDialog) {
                        val action =
                            NavGraphDirections.actionGlobalNavGoToLocationBottomSheetDialog()
                        findNavController().navigate(action)
                    }
                }

                R.id.action_alerts -> {
                    if (findNavController().currentDestination?.id != R.id.navHighwayAlertTabFragment) {
                        val action =
                            TrafficMapFragmentDirections.actionNavTrafficMapFragmentToNavHighwayAlertTabFragment()
                        findNavController().navigate(action)
                    }
                }

                R.id.action_more -> {
                    if (findNavController().currentDestination?.id != R.id.navTravelerInfoBottomSheetDialog) {
                        val action =
                            NavGraphDirections.actionGlobalNavTravelerInfoBottomSheetDialog()
                        findNavController().navigate(action)
                    }
                }

                R.id.action_travel_charts -> {
                    if (findNavController().currentDestination?.id != R.id.navTravelChartsFragment) {
                        val action = NavGraphDirections.actionGlobalNavTravelChartsFragment()
                        findNavController().navigate(action)
                    }
                }

                R.id.action_favorite -> {
                    showAddFavoriteDialog()
                }

                else -> return true
            }
        }
        return false
    }

    private fun showAddFavoriteDialog(){

        context?.let {

            val builder = AlertDialog.Builder(it)
            builder.setTitle("New Favorite Location")

            val input = EditText(it)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(input)

            val scale = resources.displayMetrics.density
            val sixteenDP = (16 * scale + 0.5f).toInt()
            val eightDP = (8 * scale + 0.5f).toInt()

            input.setPaddingRelative(sixteenDP, eightDP, sixteenDP, eightDP)

            builder.setPositiveButton("OK") {_, _ ->
                val inputText = input.text.toString()

                favoriteLocationViewModel.addFavoriteLocation(
                    inputText,
                    mMap.cameraPosition.target.latitude,
                    mMap.cameraPosition.target.longitude,
                    mMap.cameraPosition.zoom)
            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()

        }
    }

    // Location Permission

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun myLocationFine() {
        radiusCircle?.remove()
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    mMap.isMyLocationEnabled = true
                    requestLocationUpdates()
                    requestGoToLocationUpdate()
                }
        }
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun myLocationCoarse() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token).addOnSuccessListener { location : Location? ->
                mMap.isMyLocationEnabled = false
                if(location != null) {
                    circle(location)
                }
            }
            requestCoarseLocationUpdate()
        }
    }

    private fun circle(location: Location) {
        val circleOptions = CircleOptions()
            .center(LatLng(location.latitude, location.longitude))
            .radius(location.accuracy.toDouble())
            .strokeWidth(5F)
            .strokeColor(0x3571cce7)
            .fillColor(0x3571cce7)
        radiusCircle?.remove()
        radiusCircle = mMap.addCircle(circleOptions)
    }

    private fun checkAppPermissions() {

        // API 23 requires fine location alert dialog
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

                    if (Build.VERSION.SDK_INT > 30) {
                        requestLocationUpgrade = false
                    }

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
    private fun requestCoarseLocationUpdate() {

        val locationRequest = LocationRequest()
        locationRequest.numUpdates = 1

        // Request location update every 60 seconds
        locationRequest.interval = 60000

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                if(goToLocation) {
                    locationResult.lastLocation?.let { goToUsersLocation(it) }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    @SuppressLint("MissingPermission")
    private fun requestGoToLocationUpdate() {

        val locationRequest = LocationRequest()
        locationRequest.numUpdates = 1

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                if(goToLocation) {
                    locationResult.lastLocation?.let { goToUsersLocation(it) }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun goToUsersLocation(location: Location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f))
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {

        val locationRequest = LocationRequest()
        locationRequest.numUpdates = 1
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                locationResult.lastLocation?.let { checkSpeed(it) }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    // shows a safe driving message if speed is over 9 meters/second
    // and user has not seen message this session
    private fun checkSpeed(location: Location) {

        context?.let {

            val settings = PreferenceManager.getDefaultSharedPreferences(it)
            val hasSeenDrivingAlert = settings.getBoolean(getString(R.string.pref_key_has_seen_driving_message), false)

            if (!hasSeenDrivingAlert) {
                if (location.speed > 9) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("You're moving fast")
                    builder.setMessage("Please do not use the app while driving.")
                    builder.setPositiveButton("I'm a passenger") { _,_ ->
                        val editor = settings.edit()
                        editor.putBoolean(getString(R.string.pref_key_has_seen_driving_message), true)
                        editor.apply()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }

        }
    }
}


