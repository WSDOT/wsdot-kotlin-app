package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
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
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import javax.inject.Inject
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.ui.common.binding.BindingFunctions


class FerriesSailingFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var sailingViewModel: FerriesSailingViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerriesSailingFragmentBinding>()

    private var adapter by autoCleared<FerrySailingListAdapter>()

    private var currentSailingIndex = 0

   // val args: FerriesSailingFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sailingViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(FerriesSailingViewModel::class.java)
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

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
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


        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.scheduleList.layoutManager?.scrollToPosition(currentSailingIndex) // scroll to current sailing at start
            }

            override fun onChanged() {
                binding.scheduleList.layoutManager?.scrollToPosition(currentSailingIndex)
            }

        })

        sailingViewModel.sailingsWithSpaces.observe(viewLifecycleOwner, Observer { sailingResource ->
            if (sailingResource?.data != null) {

                currentSailingIndex = 0
                for ((i, sailing) in sailingResource.data.withIndex()) {
                    if (BindingFunctions.hasPassed(sailing.departingTime)) {
                        currentSailingIndex = i
                    }
                }

                Log.e("debug", sailingResource.data.toString())

                adapter.submitList(sailingResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })

    }

}