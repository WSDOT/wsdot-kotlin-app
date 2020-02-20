package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.BorderCrossingTimesFragmentBinding
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.BorderCrossingViewModel
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import java.util.*
import javax.inject.Inject

abstract class BaseCrossingTimesFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var borderCrossingViewModel: BorderCrossingViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<BorderCrossingTimesListAdapter>()

    var binding by autoCleared<BorderCrossingTimesFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MainActivity).enableAds("other")

        borderCrossingViewModel = ViewModelProvider(this, viewModelFactory)
            .get(BorderCrossingViewModel::class.java)
        borderCrossingViewModel.setCrossingDirection(getDirection())

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<BorderCrossingTimesFragmentBinding>(
            inflater,
            R.layout.border_crossing_times_fragment,
            container,
            false
        )

        binding = dataBinding

        binding.viewModel = borderCrossingViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter =
            BorderCrossingTimesListAdapter(
                dataBindingComponent,
                appExecutors,
                { crossing ->
                    borderCrossingViewModel.updateFavorite(crossing.crossingId, !crossing.favorite)
                },
                { crossing ->
                    navigateToBorderCameras(crossing)
                }
            )

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.crossingList.addItemDecoration(itemDivider)

        binding.crossingList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.crossingList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        borderCrossingViewModel.crossings.observe(viewLifecycleOwner, Observer {crossingsResource ->
            if (crossingsResource.data != null) {
                adapter.submitList(crossingsResource.data)
                if (crossingsResource.data.isEmpty() && crossingsResource.status != Status.LOADING) {
                    binding.crossingList.visibility = View.GONE
                } else {
                    binding.crossingList.visibility = View.VISIBLE
                }
            }

            if (crossingsResource.status == Status.ERROR) {
                Toast.makeText(context, getString(R.string.loading_error_message), Toast.LENGTH_SHORT).show()
            }
        })

        return dataBinding.root
    }

    private fun navigateToBorderCameras(borderCrossing: BorderCrossing) {

        if (findNavController().currentDestination?.id != R.id.navBorderCameraListFragment) {

            val action = if (borderCrossing.direction.toLowerCase(Locale.ENGLISH) == "northbound") {
                NavGraphDirections.actionGlobalNavBorderCameraListFragment(
                    northboundRoadNames[borderCrossing.route]?: "Error",
                    northboundMinLats[borderCrossing.route]?: "0.0",
                    String.format("%s", borderCrossing.name)
                )
            } else {
                NavGraphDirections.actionGlobalNavBorderCameraListFragment(
                    southboundRoadNames[borderCrossing.route]?: "Error",
                    southboundMinLats[borderCrossing.route]?: "0.0",
                    String.format("%s", borderCrossing.name)
                )
            }

            findNavController().navigate(action)
        }
    }

    abstract fun getDirection(): String

    companion object BorderCameraInfo {

        val northboundRoadNames = mapOf(
            5 to "I-5",
            543 to "SR 543",
            539 to "SR 539",
            9 to "SR 9"
        )

        val northboundMinLats = mapOf(
            5 to "48.984",
            543 to "48.987065",
            539 to "48.993886",
            9 to "48.988254"
        )

        val southboundRoadNames = mapOf<Int, String>()
        val southboundMinLats = mapOf<Int, String>()

    }


}