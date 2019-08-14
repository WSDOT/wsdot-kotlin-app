package gov.wa.wsdot.android.wsdot.db.travelerinfo

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["link"])
data class NewsRelease(
    val link: String,
    val title: String,
    val description: String,
    val pubdate: Date
)