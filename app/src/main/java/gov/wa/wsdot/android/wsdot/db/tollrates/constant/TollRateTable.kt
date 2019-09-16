package gov.wa.wsdot.android.wsdot.db.tollrates.constant

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["route"])
data class TollRateTable(
    val route: Int,
    val message: String?,
    val numCol: Int,
    val rows: List<TollRateRow>,
    val localCacheDate: Date
)