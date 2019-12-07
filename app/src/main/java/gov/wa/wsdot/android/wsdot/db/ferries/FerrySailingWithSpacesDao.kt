package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import java.util.*

@Dao
abstract class FerrySailingWithSpacesDao {

    @Query("""
        SELECT DISTINCT FerrySailing.*,
        FerrySpace.showDriveUpSpaces,
        FerrySpace.showResSpaces,
        FerrySpace.maxSpacesCount AS maxSpaces,
        FerrySpace.currentSpacesCount AS spaces,
        FerrySpace.reservableSpacesCount AS reserveSpaces,
        FerrySpace.localCacheDate AS spacesCacheDate,
        Vessel.eta AS vesselEta,
        Vessel.leftDock AS vesselLeftDock,
        Vessel.atDock AS vesselAtDock,
        Vessel.scheduledDeparture AS vesselScheduledDeparture,
        Vessel.vesselId
        FROM FerrySailing
        
        LEFT OUTER JOIN FerrySpace
        ON FerrySailing.departingTerminalId = FerrySpace.departingTerminalId
        AND FerrySailing.arrivingTerminalId = FerrySpace.arrivingTerminalId
        AND FerrySailing.departingTime = FerrySpace.departureTime
        
        LEFT OUTER JOIN Vessel
        ON FerrySailing.departingTerminalId = Vessel.departingTerminalId
        AND FerrySailing.arrivingTerminalId = Vessel.arrivingTerminalId
        AND FerrySailing.departingTime = Vessel.scheduledDeparture
        
        WHERE FerrySailing.route = (:routeId) 
        AND FerrySailing.departingTerminalId = (:departingId)
        AND FerrySailing.arrivingTerminalId = (:arrivingId) 
        AND FerrySailing.sailingDate = (:sailingDate)
    """)
    abstract fun loadSailingsWithSpaces(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date): LiveData<List<FerrySailingWithSpaces>>

}