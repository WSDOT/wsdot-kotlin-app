package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import android.util.Log
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

/**
 * A RecyclerView adapter for [FerrySailing] class.
 */
class FerrySailingListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<FerrySailing, FerrySailingItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<FerrySailing>() {
        override fun areItemsTheSame(oldItem: FerrySailing, newItem: FerrySailing): Boolean {
            return oldItem.route == newItem.route
                    && oldItem.departingTime == newItem.departingTime
                    && oldItem.arrivingTime == newItem.arrivingTime
                    && oldItem.departingTerminalId == newItem.departingTerminalId
                    && oldItem.arrivingTerminalId == newItem.arrivingTerminalId
        }

        override fun areContentsTheSame(oldItem: FerrySailing, newItem: FerrySailing): Boolean {
            return oldItem.cacheDate.time == newItem.cacheDate.time
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

    override fun bind(binding: FerrySailingItemBinding, item: FerrySailing) {
        Log.e("debug", item.arrivingTime.toString())
        binding.sailing = item
    }
}