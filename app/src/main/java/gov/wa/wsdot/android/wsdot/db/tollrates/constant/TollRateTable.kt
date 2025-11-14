package gov.wa.wsdot.android.wsdot.db.tollrates.constant

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["id"])
data class TollRateTable(
    val id: Int?,
    val route: Int,
    val message: String?,
    val numCol: Int,
    val rows: List<TollRateRow>,
    val localCacheDate: Date
)