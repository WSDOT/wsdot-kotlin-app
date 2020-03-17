package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.BorderCrossingItemBinding
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.BorderCrossingDiffCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import java.util.*

/**
 * ListAdapter for border wait items. Uses data binding layouts.
 * Used by both south and northbound wait times
 */
class BorderCrossingTimesListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val favoriteClickCallback: ((BorderCrossing) -> Unit)?,
    private val viewCamerasClickCallback: ((BorderCrossing) -> Unit)?
) : DataBoundListAdapter<BorderCrossing, BorderCrossingItemBinding>(
    appExecutors = appExecutors,
    diffCallback = BorderCrossingDiffCallback()
) {

    override fun createBinding(parent: ViewGroup): BorderCrossingItemBinding {

        val binding = DataBindingUtil.inflate<BorderCrossingItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.border_crossing_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.favoriteButton.setOnClickListener {
            binding.crossing?.let {
                favoriteClickCallback?.invoke(it)
            }
        }

        binding.crossingCamerasButton.visibility = GONE
        binding.directionView.visibility = INVISIBLE

        return binding
    }

    override fun bind(binding: BorderCrossingItemBinding, item: BorderCrossing, position: Int) {
        binding.crossing = item

        // only show if we have camera data for lane/route
        if (item.direction.toLowerCase(Locale.ENGLISH) == "northbound" ) {
            BaseCrossingTimesFragment.northboundRoadNames[binding.crossing?.route]?.let {
                binding.crossingCamerasButton.visibility = VISIBLE
                binding.crossingCamerasButton.setOnClickListener {
                    binding.crossing?.let {
                        viewCamerasClickCallback?.invoke(it)
                    }
                }
            }
        }

        if (item.direction.toLowerCase(Locale.ENGLISH) == "southbound" ) {
            BaseCrossingTimesFragment.southboundRoadNames[binding.crossing?.route]?.let {
                binding.crossingCamerasButton.visibility = VISIBLE
                binding.crossingCamerasButton.setOnClickListener {
                    binding.crossing?.let {
                        viewCamerasClickCallback?.invoke(it)
                    }
                }
            }
        }
    }
}