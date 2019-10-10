package gov.wa.wsdot.android.wsdot.ui.amtrakcascades.amtrakcascadesschedule

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.AmtrakCascadesScheduleFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.amtrakcascades.AmtrakCascadesViewModel
import gov.wa.wsdot.android.wsdot.ui.common.binding.BindingFunctions
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import java.util.*
import javax.inject.Inject

class AmtrakCascadesScheduleFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var amtrakCascadesViewModel: AmtrakCascadesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<AmtrakCascadesScheduleFragmentBinding>()

    private var adapter by autoCleared<FerrySailingListAdapter>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        amtrakCascadesViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(AmtrakCascadesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<AmtrakCascadesScheduleFragmentBinding>(
            inflater,
            R.layout.amtrak_cascades_schedule_fragment,
            container,
            false
        )

        dataBinding.amtrakViewModel = amtrakCascadesViewModel

        binding = dataBinding
        binding.lifecycleOwner = viewLifecycleOwner

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // pass function to be called on adapter item tap
        val adapter = FerrySailingListAdapter(dataBindingComponent, appExecutors)

        this.adapter = adapter

        binding.scheduleList.adapter = adapter

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

    }
}