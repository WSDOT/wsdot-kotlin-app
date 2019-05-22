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
abstract class FerryScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSchedules(schedules: List<FerrySchedule>)

    @Query("SELECT * FROM FerrySchedule")
    abstract fun loadSchedules(): LiveData<List<FerrySchedule>>

    @Query("SELECT * FROM FerrySchedule WHERE routeId = (:routeId)")
    abstract fun loadSchedule(routeId: Int): LiveData<FerrySchedule>

   // @Query("SELECT * FROM FerrySchedule WHERE id in (:scheduleIds)")
   // protected abstract fun loadById(scheduleIds: List<Int>): LiveData<List<String>>

}