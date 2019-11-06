package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesSailingFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.BindingFunctions
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import java.util.*
import javax.inject.Inject


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

    lateinit var t: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
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

        binding.lifecycleOwner = viewLifecycleOwner

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onPause() {
        super.onPause()
        // remove the auto scroll observer to prevent crash when
        // binding var is autoCleared in pager
        adapter.removeObserver()
        t.cancel()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // pass function to be called on adapter item tap
        val adapter = FerrySailingListAdapter(dataBindingComponent, appExecutors)

        this.adapter = adapter

        binding.scheduleList.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.scheduleList.layoutManager?.scrollToPosition(currentSailingIndex) // scroll to current sailing at start
            }
        })

        postponeEnterTransition()
        binding.scheduleList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        sailingViewModel.sailingsWithSpaces.observe(viewLifecycleOwner, Observer { sailingResource ->
            if (sailingResource?.data != null) {
                currentSailingIndex = 0
                for ((i, sailing) in sailingResource.data.withIndex()) {
                    if (BindingFunctions.hasPassed(sailing.departingTime)) {
                        currentSailingIndex = i
                    }
                }

                adapter.submitList(sailingResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })

        startSailingSpacesTask()

    }

    private fun startSailingSpacesTask() {
        t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    appExecutors.mainThread().execute {
                        sailingViewModel.refresh()
                    }
                }
            },
            30000,
            120000
        )
    }
}