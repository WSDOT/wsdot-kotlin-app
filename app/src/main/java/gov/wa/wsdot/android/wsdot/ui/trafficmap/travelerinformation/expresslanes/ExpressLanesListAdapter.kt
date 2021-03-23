package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation.expresslanes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.api.response.traffic.ExpressLanesStatusResponse.ExpressLanes.ExpressLaneRoute
import gov.wa.wsdot.android.wsdot.databinding.ExpressLaneItemBinding
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class ExpressLanesListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<ExpressLaneRoute, ExpressLaneItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ExpressLaneRoute>() {

        override fun areItemsTheSame(oldItem: ExpressLaneRoute, newItem: ExpressLaneRoute): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ExpressLaneRoute, newItem: ExpressLaneRoute): Boolean {
            return oldItem.status == newItem.status
                    && oldItem.title == newItem.title
                    && oldItem.updated == newItem.updated
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ExpressLaneItemBinding {

        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.express_lane_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: ExpressLaneItemBinding, item: ExpressLaneRoute, position: Int) {
        binding.expressLane = item
    }

}