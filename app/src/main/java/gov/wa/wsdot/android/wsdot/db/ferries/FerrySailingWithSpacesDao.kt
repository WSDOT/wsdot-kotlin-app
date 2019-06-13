package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import java.util.*

@Dao
abstract class FerrySailingWithSpacesDao {

    @Query("""
        SELECT FerrySailing.*,
        FerrySpace.maxSpacesCount AS maxSpaces,
        FerrySpace.currentSpacesCount AS spaces,
        FerrySpace.reservableSpacesCount,
        FerrySpace.localCacheDate AS spacesCacheDate
        FROM FerrySailing LEFT OUTER JOIN FerrySpace
        ON FerrySailing.departingTerminalId = FerrySpace.departingTerminalId
        AND FerrySailing.arrivingTerminalId = FerrySpace.arrivingTerminalId
        AND FerrySailing.departingTime = FerrySpace.departureTime
        WHERE FerrySailing.route = (:routeId) AND FerrySailing.departingTerminalId = (:departingId)
        AND FerrySailing.arrivingTerminalId = (:arrivingId) AND FerrySailing.sailingDate = (:sailingDate)
    """)
    abstract fun loadSailingsWithSpaces(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date): LiveData<List<FerrySailingWithSpaces>>

}