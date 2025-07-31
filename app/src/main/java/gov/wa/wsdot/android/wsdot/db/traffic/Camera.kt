package gov.wa.wsdot.android.wsdot.db.traffic

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["cameraId"])
data class Camera(
    val cameraId: Int,
    val title: String,
    val roadName: String,
    val milepost: Int,
    val direction: String?,
    val latitude: Double,
    val longitude: Double,
    val url: String,
    val hasVideo: Boolean,
    val localCacheDate: Date,
    var favorite: Boolean,
    val remove: Boolean
)