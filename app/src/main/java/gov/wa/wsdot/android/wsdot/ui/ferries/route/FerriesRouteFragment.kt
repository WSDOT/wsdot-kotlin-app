package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesRouteFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import gov.wa.wsdot.android.wsdot.ui.common.OpenPickerCallback
import gov.wa.wsdot.android.wsdot.ui.common.SharedDateViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class FerriesRouteFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var routeViewModel: FerriesRouteViewModel

    lateinit var dayPickerViewModel: SharedDateViewModel

    var binding by autoCleared<FerriesRouteFragmentBinding>()

    val args: FerriesRouteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        routeViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesRouteViewModel::class.java)

        dayPickerViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(SharedDateViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<FerriesRouteFragmentBinding>(
            inflater,
            R.layout.ferries_route_fragment,
            container,
            false
        )

        dataBinding.pickerCallback = object : OpenPickerCallback {
            override fun onOpenPicker() {
                val action = FerriesRouteFragmentDirections.actionNavFerriesRouteFragmentToDayPickerDialogFragment(args.title)
                findNavController().navigate(action)
            }
        }

        dataBinding.viewModel = dayPickerViewModel

        binding = dataBinding
        binding.lifecycleOwner = this

        return dataBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val routeId = args.routeId
        Log.e("debug", routeId.toString())
        Log.e("debug", "on create view")


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ferry_route_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}