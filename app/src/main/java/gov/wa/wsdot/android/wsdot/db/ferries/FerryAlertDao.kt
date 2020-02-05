package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interface for database access on Ferry Schedule related operations.
 */
@Dao
abstract class FerryAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAlerts(alerts: List<FerryAlert>)

    @Query("SELECT * FROM FerryAlert")
    abstract fun loadAlerts(): LiveData<List<FerryAlert>>

    @Query("SELECT * FROM FerryAlert WHERE route in (:routeId) ORDER BY publishDate DESC")
    abstract fun loadAlertsById(routeId: Int): LiveData<List<FerryAlert>>

    @Query("SELECT * FROM FerryAlert WHERE alertId = (:alertId) LIMIT 1")
    abstract fun loadAlertById(alertId: Int): LiveData<FerryAlert>

    @Query("DELETE FROM FerryAlert")
    abstract fun deleteOldAlerts()

    @Transaction
    open fun updateAlerts(alerts: List<FerryAlert>) {
        deleteOldAlerts()
        insertAlerts(alerts)
    }

}