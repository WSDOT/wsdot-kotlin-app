package gov.wa.wsdot.android.wsdot.ui.amtrakcascades

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.AmtrakCascadesFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.callback.TapCallback
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import gov.wa.wsdot.android.wsdot.util.autoCleared
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import java.util.*
import javax.inject.Inject

/**
 * Fragment that displays a schedule menu for Amtrak Cascades trains/bus services.
 * Uses location permission to select the origin closest to user.
 * Includes a link to the official ticket purchase site.
 */
@RuntimePermissions
class AmtrakCascadesFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var amtrakCascadesViewModel: AmtrakCascadesViewModel
    private lateinit var dayPickerViewModel: SharedDateViewModel

    private var originString = ""
    private var destinationString = ""

    // view binding - part of Android Data Binding Library
    var binding by autoCleared<AmtrakCascadesFragmentBinding>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // analytics
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as MainActivity).enableAds("other")

        // set up view models
        amtrakCascadesViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(AmtrakCascadesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        dayPickerViewModel = activity?.run {
            ViewModelProvider(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        dayPickerViewModel.setValue(Date())

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<AmtrakCascadesFragmentBinding>(
            inflater,
            R.layout.amtrak_cascades_fragment,
            container,
            false
        )

        // init origin
        amtrakCascadesViewModel.selectedOrigin.observe(viewLifecycleOwner, Observer { origin ->
            amtrakCascadesViewModel.setDeparturesQuery(origin = origin.second, destination = null)
            origin?.let { originString = it.first }
        })

        // init destination
        amtrakCascadesViewModel.selectedDestination.observe(viewLifecycleOwner, Observer { destination ->
            amtrakCascadesViewModel.setDeparturesQuery(origin = null, destination = destination.second)
            destination?.let { destinationString = it.first }
        })

        // Adjust title for the schedule screen based on user selection.
        // If user picks same origin destination, set title to just departures from origin
        dataBinding.submitButton.setOnClickListener {

            var title = "$originString to $destinationString"
            if (originString == destinationString || destinationString == "All") {
                title = "$originString Departures"
            }
            if (findNavController().currentDestination?.id != R.id.navAmtrakCascadesScheduleFragment) {
                val action =
                    AmtrakCascadesFragmentDirections.actionNavAmtrakCascadesFragmentToNavAmtrakCascadesScheduleFragment(
                        title
                    )
                findNavController().navigate(action)
            }
        }

        // Add link to the buy tickets button
        dataBinding.buyTicketsButton.setOnClickListener {
            if (findNavController().currentDestination?.id != R.id.navWebViewFragment) {
                val action = AmtrakCascadesFragmentDirections.actionGlobalNavWebViewFragment(
                    "https://www.amtrakcascades.com/buy-tickets",
                    "Buy Tickets"
                )
                findNavController().navigate(action)
            }
        }

        // bind view models to view
        dataBinding.dateViewModel = dayPickerViewModel
        dataBinding.amtrakCascadesViewModel = amtrakCascadesViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        binding = dataBinding

        initDatePicker()

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClosestStationWithPermissionCheck()
    }

    private fun initDatePicker(){

        // initialize the date picker
        dayPickerViewModel.value.observe(viewLifecycleOwner, androidx.lifecycle.Observer { date ->
            val c = Calendar.getInstance()
            if (date != null) {
                c.time = date
            }
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            amtrakCascadesViewModel.setDeparturesQuery(c.time)

        })

        // set schedule date range for today to 14 days out
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        val startTime = c.time
        c.add(Calendar.DAY_OF_YEAR, 28) // allow users to check departures up to 28 days out
        val endTime = c.time

        binding.datePickerCallback = object : TapCallback {
            override fun onTap(view: View) {
                val action = AmtrakCascadesFragmentDirections.actionNavAmtrakCascadesFragmentToDayPickerDialogFragment("", startTime.time, endTime.time)
                view.findNavController().navigate(action)
                // short delay to prevent double tap
                view.isEnabled = false
                Handler().postDelayed({ view.isEnabled = true }, 1000)
            }
        }

    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun setClosestStation() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        amtrakCascadesViewModel.selectStationNearestTo(it)
                    }
                    if (location == null) {
                        requestLocationUpdate()
                    }
                }
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_station_location_rationale, request)
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
                    amtrakCascadesViewModel.selectStationNearestTo(it)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

}