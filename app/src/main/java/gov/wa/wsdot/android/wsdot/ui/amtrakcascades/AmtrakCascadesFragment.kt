package gov.wa.wsdot.android.wsdot.ui.amtrakcascades

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.AmtrakCascadesFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.callback.TapCallback
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import gov.wa.wsdot.android.wsdot.util.autoCleared
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import java.util.*
import javax.inject.Inject

@RuntimePermissions
class AmtrakCascadesFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var amtrakCascadesViewModel: AmtrakCascadesViewModel
    lateinit var dayPickerViewModel: SharedDateViewModel


    var binding by autoCleared<AmtrakCascadesFragmentBinding>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear view models since they are no longer needed
       // viewModelStore.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set up view models
        amtrakCascadesViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(AmtrakCascadesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        dayPickerViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        dayPickerViewModel.setValue(Date())

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<AmtrakCascadesFragmentBinding>(
            inflater,
            R.layout.amtrak_cascades_fragment,
            container,
            false
        )

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

        amtrakCascadesViewModel.selectedOrigin.observe(viewLifecycleOwner, Observer { origin ->
            amtrakCascadesViewModel.setDeparturesQuery(origin = origin.second, destination = null)
        })

        amtrakCascadesViewModel.selectedDestination.observe(viewLifecycleOwner, Observer { destination ->
            amtrakCascadesViewModel.setDeparturesQuery(origin = null, destination = destination.second)
        })

        amtrakCascadesViewModel.schedulePairs.observe(viewLifecycleOwner, Observer { pairs ->
            Log.e("debug PAIRS:", pairs.toString())
        })

        dataBinding.submitButton.setOnClickListener {
            val action = AmtrakCascadesFragmentDirections.actionNavAmtrakCascadesFragmentToNavAmtrakCascadesScheduleFragment()
            findNavController().navigate(action)
        }

        // bind view models to view
        dataBinding.dateViewModel = dayPickerViewModel
        dataBinding.amtrakCascadesViewModel = amtrakCascadesViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        binding = dataBinding


        // set date range for today to 14 days out
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val startTime = c.time
        c.add(Calendar.DAY_OF_YEAR, 14)
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

        return dataBinding.root
    }


    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun setClosestTerminal() {
        context?.let { context ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        //routeViewModel.selectTerminalNearestTo(it)
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
}