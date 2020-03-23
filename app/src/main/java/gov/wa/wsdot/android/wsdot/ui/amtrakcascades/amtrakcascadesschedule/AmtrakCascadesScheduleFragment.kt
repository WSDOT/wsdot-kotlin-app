package gov.wa.wsdot.android.wsdot.ui.amtrakcascades.amtrakcascadesschedule

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.AmtrakCascadesScheduleFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.amtrakcascades.AmtrakCascadesViewModel
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.model.common.Status
import javax.inject.Inject

/**
 * Fragment that displays a list of departure and arrival items for an Amtrak schedule.
 * Needs a origin and optional destination to display times.
 * Uses the AmtrakCascadesScheduleListAdapter to display data.
 */
class AmtrakCascadesScheduleFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var amtrakCascadesViewModel: AmtrakCascadesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<AmtrakCascadesScheduleFragmentBinding>()

    private var adapter by autoCleared<AmtrakCascadesScheduleListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        amtrakCascadesViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(AmtrakCascadesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<AmtrakCascadesScheduleFragmentBinding>(
            inflater,
            R.layout.amtrak_cascades_schedule_fragment,
            container,
            false
        )

        dataBinding.amtrakViewModel = amtrakCascadesViewModel

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                amtrakCascadesViewModel.refresh()
            }
        }

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        binding = dataBinding

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap
        val adapter = AmtrakCascadesScheduleListAdapter(dataBindingComponent, appExecutors)

        this.adapter = adapter

        binding.scheduleList.adapter = adapter

        postponeEnterTransition()
        binding.scheduleList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        amtrakCascadesViewModel.schedulePairs.observe(viewLifecycleOwner, Observer { amtrakScheduleResource ->

            binding.emptyListView.visibility = View.GONE

            amtrakScheduleResource.data?.let {
                if(it.isNotEmpty() && amtrakScheduleResource.status == Status.SUCCESS) {
                    adapter.submitList(amtrakScheduleResource.data)
                    binding.emptyListView.visibility = View.GONE
                } else if (it.isEmpty() && amtrakScheduleResource.status != Status.LOADING) {
                    binding.emptyListView.visibility = View.VISIBLE
                }
            }

            if (amtrakScheduleResource.status == Status.ERROR) {
                binding.emptyListView.visibility = View.GONE
                Toast.makeText(
                    context,
                    getString(R.string.loading_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        amtrakCascadesViewModel.refresh()
    }
}