package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
abstract class FerrySailingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSailings(schedules: List<FerrySailing>)

    @Query("SELECT * FROM FerrySailing WHERE route = (:routeId)")
    abstract fun loadSailings(routeId: Int): LiveData<FerrySailing>

    @Query("SELECT * FROM FerrySailing WHERE route = (:routeId) AND departingTerminalId = (:departingId) AND arrivingTerminalId = (:arrivingId) AND sailingDate = (:sailingDate) ")
    abstract fun loadSailings(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date): LiveData<List<FerrySailing>>

    @Query("SELECT * FROM FerrySailing WHERE route = (:routeId) AND departingTerminalId = (:departingId) AND arrivingTerminalId = (:arrivingId)")
    abstract fun loadSailings(routeId: Int, departingId: Int, arrivingId: Int): LiveData<List<FerrySailing>>

    @Query("SELECT DISTINCT departingTerminalId, departingTerminalName, arrivingTerminalId, arrivingTerminalName FROM FerrySailing WHERE route = (:routeId)")
    abstract fun loadTerminalCombos(routeId: Int): LiveData<List<TerminalCombo>>

    // TODO: select by date and route ID?

}