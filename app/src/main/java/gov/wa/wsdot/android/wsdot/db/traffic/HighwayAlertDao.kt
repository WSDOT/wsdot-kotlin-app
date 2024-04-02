package gov.wa.wsdot.android.wsdot.db.traffic

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class HighwayAlertDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewHighwayAlerts(alerts: List<HighwayAlert>)

    @Query("SELECT * FROM HighwayAlert")
    abstract fun loadHighwayAlerts(): LiveData<List<HighwayAlert>>

    @Query("SELECT * FROM HighwayAlert WHERE priority = \"Highest\" OR (startLatitude == 0.0 AND startLongitude == 0.0)")
    abstract fun loadStatewideAndHighestImpactAlerts(): LiveData<List<HighwayAlert>>

    @Query("SELECT * FROM HighwayAlert WHERE alertId = :alertId")
    abstract fun loadHighwayAlert(alertId: Int): LiveData<HighwayAlert>

    @Query("SELECT * FROM HighwayAlert WHERE (displayLatitude BETWEEN :minLat AND :maxLat) AND (displayLongitude BETWEEN :minLng AND :maxLng)")
    abstract fun loadHighwayAlertsInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): LiveData<List<HighwayAlert>>

    @Transaction
    open fun updateHighwayAlerts(alerts: List<HighwayAlert>) {
        deleteOldHighwayAlerts()
        insertNewHighwayAlerts(alerts)
    }

    @Query("DELETE FROM HighwayAlert")
    abstract fun deleteOldHighwayAlerts()

}