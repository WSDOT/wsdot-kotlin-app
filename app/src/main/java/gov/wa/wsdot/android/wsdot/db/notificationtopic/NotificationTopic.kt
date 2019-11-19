package gov.wa.wsdot.android.wsdot.db.notificationtopic

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["topic"])
data class NotificationTopic (
    val topic: String,
    val title: String,
    val category: String,
    val subscribed: Boolean,
    val localCacheDate: Date,
    val remove: Boolean
)