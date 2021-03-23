package gov.wa.wsdot.android.wsdot.ui.trafficmap.bridgealerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.BridgeAlertItemBinding
import gov.wa.wsdot.android.wsdot.databinding.HighwayAlertItemBinding
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.db.travelerinfo.BridgeAlert
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class BridgeAlertListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val alertClickCallback: ((BridgeAlert) -> Unit)? // ClickCallback for item in the adapter
) : DataBoundListAdapter<BridgeAlert, BridgeAlertItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<BridgeAlert>() {
        override fun areItemsTheSame(oldItem: BridgeAlert, newItem: BridgeAlert): Boolean {
            return oldItem.alertId == newItem.alertId
        }

        override fun areContentsTheSame(oldItem: BridgeAlert, newItem: BridgeAlert): Boolean {
            return oldItem.bridge == newItem.bridge
                    && oldItem.localCacheDate == newItem.localCacheDate
                    && oldItem.description == newItem.description
                    && oldItem.openingTime == newItem.openingTime
                    && oldItem.title == newItem.title
        }
    }
) {

    override fun createBinding(parent: ViewGroup): BridgeAlertItemBinding {

        val binding = DataBindingUtil.inflate<BridgeAlertItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.bridge_alert_item,
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

    override fun bind(binding: BridgeAlertItemBinding, item: BridgeAlert, position: Int) {
        binding.alert = item
    }
}