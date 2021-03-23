package gov.wa.wsdot.android.wsdot.db.travelerinfo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class BridgeAlertDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewBridgeAlerts(alerts: List<BridgeAlert>)

    @Query("SELECT * FROM BridgeAlert")
    abstract fun loadBridgeAlerts(): LiveData<List<BridgeAlert>>

    @Query("SELECT * FROM BridgeAlert WHERE alertId = :alertId")
    abstract fun loadBridgeAlert(alertId: Int): LiveData<BridgeAlert>

    @Transaction
    open fun updateBridgeAlerts(alerts: List<BridgeAlert>) {
        deleteOldBridgeAlerts()
        insertNewBridgeAlerts(alerts)
    }

    @Query("DELETE FROM BridgeAlert")
    abstract fun deleteOldBridgeAlerts()

}