package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerrySailingItemBinding
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailing
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithSpaces

/**
 * A RecyclerView adapter for [FerrySailing] class.
 */
class FerrySailingListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<FerrySailingWithSpaces, FerrySailingItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<FerrySailingWithSpaces>() {
        override fun areItemsTheSame(oldItem: FerrySailingWithSpaces, newItem: FerrySailingWithSpaces): Boolean {
            return oldItem.route == newItem.route
                    && oldItem.departingTime == newItem.departingTime
                    && oldItem.arrivingTime == newItem.arrivingTime
                    && oldItem.departingTerminalId == newItem.departingTerminalId
                    && oldItem.arrivingTerminalId == newItem.arrivingTerminalId
                    && oldItem.spaces == newItem.spaces
                    && oldItem.maxSpaces == newItem.maxSpaces
        }

        override fun areContentsTheSame(oldItem: FerrySailingWithSpaces, newItem: FerrySailingWithSpaces): Boolean {
            return oldItem.route == newItem.route
                    && oldItem.departingTime == newItem.departingTime
                    && oldItem.arrivingTime == newItem.arrivingTime
                    && oldItem.departingTerminalId == newItem.departingTerminalId
                    && oldItem.arrivingTerminalId == newItem.arrivingTerminalId
                    && oldItem.cacheDate.time == newItem.cacheDate.time
                    && oldItem.spaces == newItem.spaces
                    && oldItem.maxSpaces == newItem.maxSpaces
        }
    }
) {

    override fun createBinding(parent: ViewGroup): FerrySailingItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.ferry_sailing_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: FerrySailingItemBinding, item: FerrySailingWithSpaces, position: Int) {
        binding.sailing = item
    }

}