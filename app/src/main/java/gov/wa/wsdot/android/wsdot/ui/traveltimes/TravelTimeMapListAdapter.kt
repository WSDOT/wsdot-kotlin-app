package gov.wa.wsdot.android.wsdot.ui.traveltimes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeMapItemBinding
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class TravelTimeMapListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val viewMapClickCallback: ((TravelTime) -> Unit)?
) : DataBoundListAdapter<TravelTime, TravelTimeMapItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<TravelTime>() {
        override fun areItemsTheSame(oldItem: TravelTime, newItem: TravelTime): Boolean {
            return oldItem.travelTimeId == newItem.travelTimeId
        }
        override fun areContentsTheSame(oldItem: TravelTime, newItem: TravelTime): Boolean {
            return oldItem.currentTime == newItem.currentTime
                    && oldItem.localCacheDate == newItem.localCacheDate
                    && oldItem.favorite == newItem.favorite
        }
    }
) {

    override fun createBinding(parent: ViewGroup): TravelTimeMapItemBinding {

        val binding = DataBindingUtil.inflate<TravelTimeMapItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.travel_time_map_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.travelTime?.let {
                viewMapClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: TravelTimeMapItemBinding, item: TravelTime, position: Int) {
        binding.travelTime = item
    }
}