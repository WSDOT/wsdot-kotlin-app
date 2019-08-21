package gov.wa.wsdot.android.wsdot.ui.trafficmap

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.maps.android.MarkerManager
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.RestAreaItem
import gov.wa.wsdot.android.wsdot.model.eventItems.GoToLocationMenuEventItem
import gov.wa.wsdot.android.wsdot.model.map.CameraClusterItem
import gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.gotolocation.GoToLocationBottomSheetFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.gotolocation.GoToLocationMenuEventListener
import gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation.TravelerInfoBottomSheetFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation.TravelerInfoMenuEventListener
import gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation.TravelerMenuItemType
import gov.wa.wsdot.android.wsdot.ui.trafficmap.restareas.RestAreaViewModel
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.map.CameraClusterManager
import gov.wa.wsdot.android.wsdot.util.map.CameraRenderer
import gov.wa.wsdot.android.wsdot.util.putDouble
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject

@RuntimePermissions
class TrafficMapFragment : DaggerFragment(), Injectable , OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    ClusterManager.OnClusterItemClickListener<CameraClusterItem>,
    ClusterManager.OnClusterClickListener<CameraClusterItem>,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
    SpeedDialView.OnActionSelectedListener, Toolbar.OnMenuItemClickListener,
    GoToLocationMenuEventListener, TravelerInfoMenuEventListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mapHighwayAlertsViewModel: MapHighwayAlertsViewModel
    lateinit var mapCamerasViewModel: MapCamerasViewModel
    lateinit var restAreaViewModel: RestAreaViewModel

    // Maps markers to their underlying data
    private val highwayAlertMarkers = HashMap<Marker, HighwayAlert>()
    private val restAreaMarkers = HashMap<Marker, RestAreaItem>()
    private val cameraClusterItems = mutableListOf<CameraClusterItem>()

    var showAlerts: Boolean = true
    var showRestAreas: Boolean = true

    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Clusters
    private lateinit var mCameraClusterManager: CameraClusterManager
    private lateinit var mMarkerManager: MarkerManager

    // FAB
    private lateinit var mFab: SpeedDialView

    // Update Tasks
    private lateinit var mapUpdateHandler: Handler
    private val alertsUpdateTask = object: Runnable {
        override fun run() {
            mapHighwayAlertsViewModel.refresh()
            mapUpdateHandler.postDelayed(this, 300000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapUpdateHandler = Handler(Looper.getMainLooper())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.map_fragment, container, false)

        initBottomBar(rootView)

        mapHighwayAlertsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MapHighwayAlertsViewModel::class.java)

        mapCamerasViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MapCamerasViewModel::class.java)

        restAreaViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(RestAreaViewModel::class.java)

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        showAlerts = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_highway_alerts), true)
        showRestAreas = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_rest_areas), true)

        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initSettingsFAB(rootView)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mapUpdateHandler.post(alertsUpdateTask)
    }

    override fun onPause() {
        super.onPause()
        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = settings.edit()
        editor.putDouble(getString(R.string.user_preference_traffic_map_latitude), mMap.projection.visibleRegion.latLngBounds.center.latitude)
        editor.putDouble(getString(R.string.user_preference_traffic_map_longitude), mMap.projection.visibleRegion.latLngBounds.center.longitude)
        editor.putFloat(getString(R.string.user_preference_traffic_map_zoom), mMap.cameraPosition.zoom)
        editor.apply()

        mapUpdateHandler.removeCallbacks(alertsUpdateTask)

    }

    override fun onMapReady(map: GoogleMap?) {

        mMap = map as GoogleMap

        mMap.clear()

        mMap.uiSettings.isCompassEnabled = true
        mMap.isTrafficEnabled = true

        enableMyLocationWithPermissionCheck()

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val latitude = settings.getDouble(getString(R.string.user_preference_traffic_map_latitude), 47.6062)
        val longitude = settings.getDouble(getString(R.string.user_preference_traffic_map_longitude), -122.3321)
        val zoom = settings.getFloat(getString(R.string.user_preference_traffic_map_zoom), 12.0f)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))

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


        // init a new collection for alert markers
        mMarkerManager.newCollection(getString(R.string.highway_alert_marker_collection_id))
        mMarkerManager.getCollection(getString(R.string.highway_alert_marker_collection_id)).setOnMarkerClickListener(this)

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

                    var alertIcon: BitmapDescriptor

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

        highwayAlertMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavHighwayAlertFragment(it.alertId, it.category)
            findNavController().navigate(action)
            return true
        }

        restAreaMarkers[marker]?.let {
            val action = NavGraphDirections.actionGlobalNavRestAreaFragment(it.description)
            findNavController().navigate(action)
            return true
        }

        return true
    }


    // functions to manager non-clustered marker collections

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
        p0?.let {
            val action = NavGraphDirections.actionGlobalNavCameraFragment(it.mCamera.cameraId, it.mCamera.title)
            findNavController().navigate(action)
        }
        return true
    }

    // Called when a cluster that represents multiple markers is clicked
    override fun onClusterClick(p0: Cluster<CameraClusterItem>?): Boolean {

        p0?.let { clusterItems ->

            // Open if cluster has 10 or less items?
            if (clusterItems.size <= 5) {
                val action = NavGraphDirections.actionGlobalNavCameraListFragment(clusterItems.items.map { it.mCamera.cameraId }.toIntArray(), "Traffic Cameras")
                findNavController().navigate(action)
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

        mFab.setOnActionSelectedListener(this)

    }

    private fun getHighwayAlertsVisibilityAction(): SpeedDialActionItem  {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_highway_alerts), true)

        var actionColor = resources.getColor(R.color.colorPrimary)
        var icon = R.drawable.ic_layers

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_highway_alert_visibility_action, icon)
            .setLabel(R.string.fab_highway_alerts_label)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getRestAreasVisibilityAction(): SpeedDialActionItem  {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_rest_areas), true)

        var actionColor = resources.getColor(R.color.colorPrimary)
        var icon = R.drawable.ic_layers

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_rest_area_visibility_action, icon)
            .setLabel(R.string.fab_rest_areas_label)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getCameraVisibilityAction(): SpeedDialActionItem {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_show_cameras), true)
        var actionColor = resources.getColor(R.color.colorPrimary)
        var icon = R.drawable.ic_layers

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
            icon = R.drawable.ic_layers_off
        }

        return SpeedDialActionItem.Builder(R.id.fab_camera_visibility_action, icon)
            .setLabel(R.string.fab_camera_label)
            .setFabBackgroundColor(actionColor)
            .create()

    }

    private fun getCameraClusterAction(): SpeedDialActionItem {

        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        val showCameras = settings.getBoolean(getString(R.string.user_preference_traffic_map_cluster_cameras), true)
        var actionColor = resources.getColor(R.color.colorPrimary)
        var icon = R.drawable.ic_done

        if (!showCameras) {
            actionColor = resources.getColor(R.color.gray)
        }

        return SpeedDialActionItem.Builder(R.id.fab_camera_cluster_action, icon)
            .setLabel(R.string.fab_camera_Clusters_label)
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
                    editor.apply()

                    mCameraClusterManager.markerCollection?.markers?.let{ markers ->
                        for (marker in markers) { marker.remove() }
                    }
                    mCameraClusterManager.clusterMarkerCollection?.markers?.let{markers ->
                        for (marker in markers) { marker.remove() }
                    }

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
    }

    // For bottom menu
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.let {
            when(it.itemId) {
                R.id.action_refresh -> {
                    mapHighwayAlertsViewModel.refresh()
                    mapCamerasViewModel.refresh()
                    val toast = Toast.makeText(context, "refreshing...", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }

                R.id.action_alerts -> {
                    Log.e("debug", "alerts")
                }

                R.id.action_go_to_location -> {
                    fragmentManager?.let { fragmentManagerValue ->
                        if (fragmentManagerValue.findFragmentByTag("go_to_location_bottom_sheet") == null) {
                            val goToLocationBottomSheet =
                                GoToLocationBottomSheetFragment(this)
                            goToLocationBottomSheet.show(fragmentManagerValue, "go_to_location_bottom_sheet")
                        }
                    }
                }

                R.id.action_favorite -> {
                    Log.e("debug", "fav")
                }

                R.id.action_more -> {
                    fragmentManager?.let { fragmentManagerValue ->
                        if (fragmentManagerValue.findFragmentByTag("traveler_info_bottom_sheet") == null) {
                            val travelerInfoBottomSheetFragment =
                                TravelerInfoBottomSheetFragment(this)
                            travelerInfoBottomSheetFragment.show(fragmentManagerValue, "traveler_info_bottom_sheet")
                        }
                    }
                }
                else -> return true
            }
        }
        return false
    }
     
    // GoToLocationListener
    override fun goToLocation(goToLocationItem: GoToLocationMenuEventItem) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(goToLocationItem.location, goToLocationItem.zoom))
    }

    // Traveler Info Menu Listener
    override fun travelerInfoMenuEvent(eventType: TravelerMenuItemType) {
        when (eventType) {
            TravelerMenuItemType.TRAVEL_TIMES -> {
                Log.e("debug", "travel times")

            }
            TravelerMenuItemType.NEWS_ITEMS -> {
                Log.e("debug", "news")

            }
            TravelerMenuItemType.EXPRESS_LANES -> {
                val action = NavGraphDirections.actionGlobalNavWebViewFragment("https://www.wsdot.wa.gov/travel/operations-services/express-lanes/home", "Express Lanes")
                findNavController().navigate(action)
            }
            TravelerMenuItemType.SOCIAL_MEDIA -> {
                Log.e("debug", "social")

            }
            TravelerMenuItemType.TRAVEL_CHARTS -> {
                Log.e("debug", "charts")

            }
            TravelerMenuItemType.COMMERCIAL_VEHICLE_RESTRICTIONS -> {
                val action = NavGraphDirections.actionGlobalNavWebViewFragment("https://www.wsdot.com/Small/CV/", "Restrictions")
                findNavController().navigate(action)
            }
        }
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

