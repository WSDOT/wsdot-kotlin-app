package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Interface for database access on Ferry Schedule related operations.
 */
@Dao
abstract class FerryAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAlerts(schedules: List<FerryAlert>)

    @Query("SELECT * FROM FerryAlert")
    abstract fun loadAlerts(): LiveData<List<FerryAlert>>

    @Query("SELECT * FROM FerryAlert WHERE route in (:routeId) ORDER BY publishDate DESC")
    abstract fun loadAlertsById(routeId: Int): LiveData<List<FerryAlert>>

    @Query("SELECT * FROM FerryAlert WHERE alertId = (:alertId) LIMIT 1")
    abstract fun loadAlertById(alertId: Int): LiveData<FerryAlert>

}