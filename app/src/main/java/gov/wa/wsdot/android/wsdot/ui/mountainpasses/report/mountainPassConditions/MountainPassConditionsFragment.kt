package gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.mountainPassConditions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassConditionsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.MountainPassReportViewModel
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class MountainPassConditionsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var passReportViewModel: MountainPassReportViewModel

    var binding by autoCleared<MountainPassConditionsFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        passReportViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(MountainPassReportViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<MountainPassConditionsFragmentBinding>(
            inflater,
            R.layout.mountain_pass_conditions_fragment,
            container,
            false
        )

        binding = dataBinding

        passReportViewModel.pass.observe(viewLifecycleOwner, Observer { pass ->
            if (pass.data != null) {


                Log.e("debug", "test")
                Log.e("debug", pass.data.weatherCondition)

                binding.pass = pass.data
            }
        })

        dataBinding.lifecycleOwner = viewLifecycleOwner

        return dataBinding.root
    }
}