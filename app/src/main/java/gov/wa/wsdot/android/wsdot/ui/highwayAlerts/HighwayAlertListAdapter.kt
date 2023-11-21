package gov.wa.wsdot.android.wsdot.ui.highwayAlerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.HighwayAlertItemBinding
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class HighwayAlertListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val alertClickCallback: ((HighwayAlert) -> Unit)? // ClickCallback for item in the adapter
) : DataBoundListAdapter<HighwayAlert, HighwayAlertItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HighwayAlert>() {
        override fun areItemsTheSame(oldItem: HighwayAlert, newItem: HighwayAlert): Boolean {
            return oldItem.alertId == newItem.alertId
        }

        override fun areContentsTheSame(oldItem: HighwayAlert, newItem: HighwayAlert): Boolean {
            return oldItem.headline == newItem.headline
                    && oldItem.localCacheDate == newItem.localCacheDate
                    && oldItem.travelCenterPriorityId == newItem.travelCenterPriorityId
                    && oldItem.startLatitude == newItem.startLatitude
                    && oldItem.startLongitude == newItem.startLongitude
        }
    }
) {

    override fun createBinding(parent: ViewGroup): HighwayAlertItemBinding {

        val binding = DataBindingUtil.inflate<HighwayAlertItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.highway_alert_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.alert_view).setOnClickListener {
            binding.alert?.let {
                alertClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: HighwayAlertItemBinding, item: HighwayAlert, position: Int) {
        binding.alert = item
    }
}