package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class FerrySailingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSailings(sailings: List<FerrySailing>)

    @Query("SELECT DISTINCT departingTerminalId, departingTerminalName, arrivingTerminalId, arrivingTerminalName FROM FerrySailing WHERE route = (:routeId)")
    abstract fun loadTerminalCombos(routeId: Int): LiveData<List<TerminalCombo>>

    @Query("SELECT MIN(sailingDate) AS startDate, MAX(sailingDate) AS endDate FROM FerrySailing WHERE route = (:routeId)")
    abstract fun loadScheduleDateRange(routeId: Int): LiveData<FerryScheduleRange>

    @Query("DELETE FROM FerrySailing")
    abstract fun deleteOldSailings()

    @Transaction
    open fun updateSailings(sailings: List<FerrySailing>) {
        deleteOldSailings()
        insertSailings(sailings)
    }

}