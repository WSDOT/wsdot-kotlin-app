package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerrySailingItemBinding
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailing
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithSpaces
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

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
            return oldItem.departingTime == newItem.departingTime
                    && oldItem.departingTerminalId == newItem.departingTerminalId
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
                    && oldItem.spacesCacheDate == newItem.spacesCacheDate
                    && oldItem.vesselAtDock == newItem.vesselAtDock
                    && oldItem.vesselEta == newItem.vesselEta
                    && oldItem.vesselLeftDock == newItem.vesselLeftDock
        }
    }
) {

    var mObserver: RecyclerView.AdapterDataObserver? = null

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        mObserver = observer
        super.registerAdapterDataObserver(observer)

    }

    // This lets us add an observer that is dependant on the binding var
    // in this case we remove our auto scroll observer
    fun removeObserver() {
        mObserver?.let {
            unregisterAdapterDataObserver(it)
            mObserver = null
        }
    }

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