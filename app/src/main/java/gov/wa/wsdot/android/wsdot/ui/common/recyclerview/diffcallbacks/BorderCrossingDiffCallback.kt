package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing

class BorderCrossingDiffCallback: DiffUtil.ItemCallback<BorderCrossing>() {
    override fun areItemsTheSame(oldItem: BorderCrossing, newItem: BorderCrossing): Boolean {
        return oldItem.crossingId == newItem.crossingId
    }

    override fun areContentsTheSame(oldItem: BorderCrossing, newItem: BorderCrossing): Boolean {
        return oldItem.crossingId == newItem.crossingId
                && oldItem.wait == newItem.wait
                && oldItem.direction == newItem.direction
                && oldItem.name == newItem.name
                && oldItem.lane == newItem.lane
                && oldItem.localCacheDate == newItem.localCacheDate
                && oldItem.favorite == newItem.favorite
    }
}