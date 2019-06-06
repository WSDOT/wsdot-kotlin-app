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

    @Query("SELECT DISTINCT departingTerminalId, departingTerminalName, arrivingTerminalId, arrivingTerminalName FROM FerrySailing WHERE route = (:routeId)")
    abstract fun loadTerminalCombos(routeId: Int): LiveData<List<TerminalCombo>>

    @Query("SELECT MIN(sailingDate) AS startDate, MAX(sailingDate) AS endDate FROM FerrySailing WHERE route = (:routeId)")
    abstract fun loadScheduleDateRange(routeId: Int): LiveData<FerryScheduleRange>

}