package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule

class FerryScheduleDiffCallback: DiffUtil.ItemCallback<FerrySchedule>() {
    override fun areItemsTheSame(oldItem: FerrySchedule, newItem: FerrySchedule): Boolean {
        return oldItem.routeId == newItem.routeId
    }

    override fun areContentsTheSame(oldItem: FerrySchedule, newItem: FerrySchedule): Boolean {
        return oldItem.description == newItem.description
                && oldItem.serverCacheDate.time == newItem.serverCacheDate.time
                && oldItem.favorite == newItem.favorite
    }
}