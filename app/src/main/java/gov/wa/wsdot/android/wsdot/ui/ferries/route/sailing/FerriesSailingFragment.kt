package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.databinding.DataBindingUtil
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesSailingFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import javax.inject.Inject


class FerriesSailingFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var sailingViewModel: FerriesSailingViewModel
    lateinit var dayPickerViewModel: SharedDateViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerriesSailingFragmentBinding>()

    private var adapter by autoCleared<FerrySailingListAdapter>()

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

        dayPickerViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<FerriesSailingFragmentBinding>(
            inflater,
            R.layout.ferries_sailing_fragment,
            container,
            false
        )

        dataBinding.sailingViewModel = sailingViewModel

        binding = dataBinding

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap
        val adapter = FerrySailingListAdapter(dataBindingComponent, appExecutors)

        this.adapter = adapter

        binding.scheduleList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.scheduleList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }




        sailingViewModel.sailings.observe(viewLifecycleOwner, Observer { sailingResource ->
            if (sailingResource?.data != null) {
                adapter.submitList(sailingResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }



}