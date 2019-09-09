package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.traffic.Camera

class CameraDiffCallback: DiffUtil.ItemCallback<Camera>() {
    override fun areItemsTheSame(oldItem: Camera, newItem: Camera): Boolean {
        return oldItem.cameraId == newItem.cameraId
    }

    override fun areContentsTheSame(oldItem: Camera, newItem: Camera): Boolean {
        return oldItem.favorite == newItem.favorite &&
                oldItem.url == newItem.url &&
                oldItem.localCacheDate == newItem.localCacheDate &&
                oldItem.title == newItem.title
    }
}