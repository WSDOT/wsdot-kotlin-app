package gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerryAlertItemBinding
import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlert
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [FerryAlert] class.
 */
class FerryAlertsListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val alertClickCallback: ((FerryAlert) -> Unit)?
) : DataBoundListAdapter<FerryAlert, FerryAlertItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<FerryAlert>() {
        override fun areItemsTheSame(oldItem: FerryAlert, newItem: FerryAlert): Boolean {
            return oldItem.alertId == newItem.alertId
        }

        override fun areContentsTheSame(oldItem: FerryAlert, newItem: FerryAlert): Boolean {
            return oldItem.description == newItem.description
        }
    }
) {

    override fun createBinding(parent: ViewGroup): FerryAlertItemBinding {

        val binding = DataBindingUtil.inflate<FerryAlertItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.ferry_alert_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.alert?.let {
                alertClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: FerryAlertItemBinding, item: FerryAlert, position: Int) {
        binding.alert = item
    }
}