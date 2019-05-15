package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity

@Entity(primaryKeys = ["routeId"])
data class FerrySchedule(

    val routeId: Int,
    val description: String,
    val crossingTime: Int,
    val cacheDate: String

)