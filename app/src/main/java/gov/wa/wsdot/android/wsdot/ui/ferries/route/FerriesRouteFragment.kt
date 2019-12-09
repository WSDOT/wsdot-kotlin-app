package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesRouteFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.SimpleFragmentPagerAdapter
import gov.wa.wsdot.android.wsdot.ui.common.callback.TapCallback
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertsViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras.TerminalCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselWatchFragment
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.putDouble
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import java.util.Calendar.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@RuntimePermissions
class FerriesRouteFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var routeViewModel: FerriesRouteViewModel
    lateinit var sailingViewModel: FerriesSailingViewModel
    lateinit var ferryAlertsViewModel: FerryAlertsViewModel

    lateinit var dayPickerViewModel: SharedDateViewModel

    private var isFavorite: Boolean = false

    private lateinit var fragmentPagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager

    var binding by autoCleared<FerriesRouteFragmentBinding>()

    val args: FerriesRouteFragmentArgs by navArgs()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        setVesselWatchView(args.routeId)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear view models since they are no longer needed
        activity?.viewModelStore?.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set up view models
        routeViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesRouteViewModel::class.java)
        routeViewModel.setRouteId(args.routeId)

        dayPickerViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        sailingViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(FerriesSailingViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // set alert route ID for pager fragment
        ferryAlertsViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(FerryAlertsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        ferryAlertsViewModel.setFerryAlertsRouteQuery(args.routeId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<FerriesRouteFragmentBinding>(
            inflater,
            R.layout.ferries_route_fragment,
            container,
            false
        )

        // obverse all available terminal combos for the route ONCE.
        // this allows us to set the initial value for the two-way bound data.
        routeViewModel.terminals.observe(viewLifecycleOwner, Observer { terminals ->
            if (terminals.data != null) {
                routeViewModel.selectedTerminalCombo.value = terminals.data[0]
                routeViewModel.terminals.removeObservers(viewLifecycleOwner)
            }
        })

        // observe the schedule item to update the favorite icon
        routeViewModel.route.observe(viewLifecycleOwner, Observer { schedule ->
            if (schedule.data != null) {
                isFavorite = schedule.data.favorite
                activity?.invalidateOptionsMenu()
            }

        })
        // observe terminal combo changes. the terminalCombo is two way data bound to the UI selector
        routeViewModel.selectedTerminalCombo.observe(viewLifecycleOwner, Observer { terminalCombo ->
            sailingViewModel.setSailingQuery(args.routeId, terminalCombo.departingTerminalId, terminalCombo.arrivingTerminalId)
        })

        // observe shared value to update sailing query when a new date is selected
        dayPickerViewModel.value.observe(viewLifecycleOwner, Observer { date ->

            val c = getInstance()
            if (date != null) {
                c.time = date
            }
            c.set(HOUR_OF_DAY, 0)
            c.set(MINUTE, 0)
            c.set(SECOND, 0)
            c.set(MILLISECOND, 0)
            sailingViewModel.setSailingQuery(c.time)
        })

        // bind view models to view
        dataBinding.dateViewModel = dayPickerViewModel
        dataBinding.routeViewModel = routeViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        binding = dataBinding

        // Observe schedule range for day picker values
        routeViewModel.scheduleRange.observe(viewLifecycleOwner, Observer { scheduleRange ->
            binding.datePickerCallback = object : TapCallback {
                override fun onTap(view: View) {
                    val action = FerriesRouteFragmentDirections.actionNavFerriesRouteFragmentToDayPickerDialogFragment(args.title, scheduleRange.startDate.time, scheduleRange.endDate.time)
                    view.findNavController().navigate(action)
                    // short delay to prevent double tap
                    view.isEnabled = false
                    Handler().postDelayed({ view.isEnabled = true }, 1000)
                }
            }
        })

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewPager = view.findViewById(R.id.pager)
        setupViewPager(viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        setClosestTerminalWithPermissionCheck()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        setFavoriteMenuIcon(menu.findItem(R.id.action_favorite))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                routeViewModel.updateFavorite(args.routeId)
                return false
            }
            else -> {}
        }
        return false
    }

    private fun setFavoriteMenuIcon(menuItem: MenuItem){
        if (isFavorite) {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_pink, null)
        } else {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_outline, null)
        }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager) {

        val fragments = ArrayList<Fragment>()
        fragments.add(FerriesSailingFragment())
        fragments.add(TerminalCamerasListFragment())
        fragments.add(FerryAlertsFragment())
        fragments.add(VesselWatchFragment())

        val titles = ArrayList<String>()
        titles.add("sailings")
        titles.add("cameras")
        titles.add("alerts")
        titles.add("vessel watch")

        fragmentPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, fragments, titles)

        viewPager.adapter = fragmentPagerAdapter

    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun setClosestTerminal() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        routeViewModel.selectTerminalNearestTo(it)
                    }
                    if (location == null) {
                        requestLocationUpdate()
                    }
                }
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_terminal_location_rationale, request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
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

    private fun requestLocationUpdate() {

        val locationRequest = LocationRequest()
        locationRequest.numUpdates = 1

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                locationResult.locations.first()?.let {
                    routeViewModel.selectTerminalNearestTo(it)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun setVesselWatchView(routeId: Int){

        var latitude = 47.583571
        var longitude = -122.473468
        var zoom = 10f

        when (routeId) {
            // Ana-SJ-Sid
            272, 9, 10 -> {
                latitude = 48.550921
                longitude = -122.840836
                zoom = 10f
            }
            // Ed-King
            6 -> {
                latitude = 47.803096
                longitude = -122.438718
                zoom = 12f
            }
            // F-S-V
            13, 14, 15 -> {
                latitude = 47.513625
                longitude = -122.450820
                zoom = 12f
            }
            // Muk-Cl
            7 -> {
                latitude = 47.963857
                longitude = -122.327721
                zoom = 12f
            }
            // Pt-Key
            8 -> {
                latitude = 48.135562
                longitude = -122.714449
                zoom = 12f
            }
            // Pd-Tal
            1 -> {
                latitude = 47.319040
                longitude = -122.510890
                zoom = 13f
            }
            // Sea-Bi
            5 -> {
                latitude = 47.600325
                longitude = -122.437249
                zoom = 11f
            }
            // Sea-Br
            3 -> {
                latitude = 47.565125
                longitude = -122.480508
                zoom = 10f
            }
        }

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = settings.edit()
        editor.putDouble(getString(R.string.user_preference_vessel_watch_latitude), latitude)
        editor.putDouble(getString(R.string.user_preference_vessel_watch_longitude), longitude)
        editor.putFloat(getString(R.string.user_preference_vessel_watch_zoom), zoom)
        editor.commit()
    }
}