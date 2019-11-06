package gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passConditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassConditionsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.MountainPassReportViewModel
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject

class PassConditionsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var passReportViewModel: MountainPassReportViewModel

    var binding by autoCleared<MountainPassConditionsFragmentBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

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
                binding.pass = pass.data
            }
            if (pass.status == Status.ERROR) {
                Toast.makeText(context, getString(R.string.loading_error_message), Toast.LENGTH_SHORT).show()
            }
        })

        dataBinding.lifecycleOwner = viewLifecycleOwner

        return dataBinding.root
    }
}