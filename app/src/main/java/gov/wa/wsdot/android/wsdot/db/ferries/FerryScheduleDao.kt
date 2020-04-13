package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

/**
 * Interface for database access on Ferry Schedule related operations.
 */
@Dao
abstract class FerryScheduleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewSchedules(schedules: List<FerrySchedule>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(schedules: List<FerrySchedule>)

    @Query("SELECT * FROM FerrySchedule ORDER BY FerrySchedule.description ASC")
    abstract fun loadSchedules(): LiveData<List<FerrySchedule>>

    @Query("SELECT * FROM FerrySchedule WHERE favorite = :isFavorite")
    abstract fun loadFavoriteSchedules(isFavorite: Boolean = true): LiveData<List<FerrySchedule>>

    @Query("SELECT * FROM FerrySchedule WHERE routeId = (:routeId)")
    abstract fun loadSchedule(routeId: Int): LiveData<FerrySchedule>

    @Query("UPDATE FerrySchedule SET favorite = :isFavorite WHERE routeId = :routeId")
    abstract fun updateFavorite(routeId: Int, isFavorite: Boolean)

    @Transaction
    open fun updateSchedules(schedules: List<FerrySchedule>) {
        markSchedulesForRemoval()
        for (schedule in schedules) {
            updateSchedules(
                schedule.routeId,
                schedule.description,
                schedule.crossingTime,
                schedule.serverCacheDate,
                schedule.localCacheDate,
                false)
        }
        deleteOldSchedules()
        insertNewSchedules(schedules)
    }

    @Query("UPDATE FerrySchedule SET remove = 1")
    abstract fun markSchedulesForRemoval()

    @Query("""
        UPDATE FerrySchedule SET
        description = :description,
        crossingTime = :crossingTime,
        serverCacheDate = :serverCacheDate,
        localCacheDate = :localCacheDate,
        remove = :remove
        WHERE routeId = :routeId
    """)
    abstract fun updateSchedules(routeId: Int, description: String, crossingTime: Int, serverCacheDate: Date, localCacheDate: Date, remove: Boolean)

    @Query("DELETE FROM FerrySchedule WHERE remove = 1")
    abstract fun deleteOldSchedules()

}