package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.VesselDetailsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import javax.inject.Inject
import androidx.core.net.toUri


class VesselDetailsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var vesselDetailsViewModel: VesselDetailsViewModel

    val args: VesselDetailsFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.ferry_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_website -> {

                val uri = String.format(
                    "https://www.wsdot.com/ferries/vesselwatch/VesselDetail.aspx?vessel_id=%s",
                    args.vesselId).toUri()

                startActivity(Intent(Intent.ACTION_VIEW, uri))

                return false
            }
            else -> {}
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        vesselDetailsViewModel = ViewModelProvider(this, viewModelFactory)
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