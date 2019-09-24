package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign

class TollSignDiffCallback: DiffUtil.ItemCallback<TollSign>() {

    override fun areItemsTheSame(oldItem: TollSign, newItem: TollSign): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TollSign, newItem: TollSign): Boolean {

        if (oldItem.favorite != newItem.favorite ||
            oldItem.travelDirection != newItem.travelDirection ||
            oldItem.locationName != newItem.locationName ||
            oldItem.milepost != newItem.milepost ||
            oldItem.startLatitude != newItem.startLatitude ||
            oldItem.startLongitude != newItem.startLongitude ||
            oldItem.stateRoute != newItem.stateRoute ||
            oldItem.localCacheDate != newItem.localCacheDate
        ) {
            return false
        }

        if (oldItem.trips.size != newItem.trips.size) { return false }

        for ((index, oldTrip) in oldItem.trips.withIndex()) {
            if (oldTrip.currentRate != newItem.trips[index].currentRate ||
                oldTrip.tripName != newItem.trips[index].tripName ||
                oldTrip.message != newItem.trips[index].message ||
                oldTrip.endLatitude != newItem.trips[index].endLatitude ||
                oldTrip.endLongitude != newItem.trips[index].endLongitude ||
                oldTrip.endMilepost != newItem.trips[index].endMilepost
            ) {
                return false
            }
        }
        return true
    }
}