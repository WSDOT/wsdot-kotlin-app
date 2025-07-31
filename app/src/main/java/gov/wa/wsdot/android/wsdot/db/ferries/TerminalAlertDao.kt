package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interface for database access on Ferry Schedule related operations.
 */
@Dao
abstract class TerminalAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTerminals(alerts: List<TerminalAlert>)

    @Query("SELECT * FROM TerminalAlert")
    abstract fun loadTerminals(): LiveData<List<TerminalAlert>>

    @Query("SELECT * FROM TerminalAlert WHERE TerminalAlert.terminalID = (:terminalID)")
    abstract fun loadTerminal(terminalID: Int): LiveData<TerminalAlert>



    @Query("DELETE FROM TerminalAlert")
    abstract fun deleteOldAlerts()

    @Transaction
    open fun updateAlerts(alerts: List<TerminalAlert>) {
        deleteOldAlerts()
        insertTerminals(alerts)
    }

}