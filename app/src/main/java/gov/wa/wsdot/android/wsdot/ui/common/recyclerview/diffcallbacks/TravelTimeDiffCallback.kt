package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime

class TravelTimeDiffCallback: DiffUtil.ItemCallback<TravelTime>() {
    override fun areItemsTheSame(oldItem: TravelTime, newItem: TravelTime): Boolean {
        return oldItem.travelTimeId == newItem.travelTimeId
    }

    override fun areContentsTheSame(oldItem: TravelTime, newItem: TravelTime): Boolean {
        return oldItem.favorite == newItem.favorite &&
                oldItem.currentTime == newItem.currentTime &&
                oldItem.via == newItem.via &&
                oldItem.updated == newItem.updated &&
                oldItem.localCacheDate == newItem.localCacheDate
    }

}