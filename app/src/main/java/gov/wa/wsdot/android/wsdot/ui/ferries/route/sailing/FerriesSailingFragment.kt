package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesSailingFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Inject


class FerriesSailingFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var sailingViewModel: FerriesSailingViewModel

    //var fragmentDataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerriesSailingFragmentBinding>()

   // val args: FerriesSailingFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sailingViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesSailingViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<FerriesSailingFragmentBinding>(
            inflater,
            R.layout.ferries_sailing_fragment,
            container,
            false
        )

        dataBinding.sailingViewModel = sailingViewModel

        binding = dataBinding

        return dataBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       // val routeId = args.routeId
       // Log.e("debug", routeId.toString())
    }



}