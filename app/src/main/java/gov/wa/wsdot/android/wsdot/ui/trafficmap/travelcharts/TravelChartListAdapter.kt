package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelChartsStatusResponse.ChartRoute.TravelChart
import gov.wa.wsdot.android.wsdot.databinding.TravelChartItemBinding
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class TravelChartListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<TravelChart, TravelChartItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<TravelChart>() {
        override fun areItemsTheSame(oldItem: TravelChart, newItem: TravelChart): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }
        override fun areContentsTheSame(oldItem: TravelChart, newItem: TravelChart): Boolean {
            return oldItem.altText != newItem.altText
                    && oldItem.imageUrl == newItem.imageUrl
        }
    }
) {

    override fun createBinding(parent: ViewGroup): TravelChartItemBinding {

        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.travel_chart_item,
            parent,
            false,
            dataBindingComponent
        )

    }

    override fun bind(binding: TravelChartItemBinding, item: TravelChart, position: Int) {
        binding.travelChart = item
    }
}