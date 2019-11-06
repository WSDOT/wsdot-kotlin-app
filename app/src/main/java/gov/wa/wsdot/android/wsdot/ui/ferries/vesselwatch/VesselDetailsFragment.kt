package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.VesselDetailsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import javax.inject.Inject

class VesselDetailsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var vesselDetailsViewModel: VesselDetailsViewModel

    val args: VesselDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        vesselDetailsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(VesselDetailsViewModel::class.java)
        vesselDetailsViewModel.setVesselQuery(args.vesselId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<VesselDetailsFragmentBinding>(
            inflater,
            R.layout.vessel_details_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.vesselViewModel = vesselDetailsViewModel

        return dataBinding.root
    }


}