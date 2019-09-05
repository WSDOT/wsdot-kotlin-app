package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass

class MountainPassDiffCallback: DiffUtil.ItemCallback<MountainPass>() {

    override fun areItemsTheSame(oldItem: MountainPass, newItem: MountainPass): Boolean {
        return oldItem.passId == newItem.passId
    }

    override fun areContentsTheSame(oldItem: MountainPass, newItem: MountainPass): Boolean {
        return oldItem.passName == newItem.passName
                && oldItem.forecasts == newItem.forecasts
                && oldItem.weatherCondition == newItem.weatherCondition
                && oldItem.serverCacheDate.time == newItem.serverCacheDate.time
                && oldItem.favorite == newItem.favorite
    }
}