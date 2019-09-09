package gov.wa.wsdot.android.wsdot.db.bordercrossing

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
abstract class BorderCrossingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewCrossings(crossings: List<BorderCrossing>)

    @Query("SELECT * FROM BorderCrossing WHERE direction = :direction")
    abstract fun loadCrossingsForDirection(direction: String): LiveData<List<BorderCrossing>>

    @Query("SELECT * FROM BorderCrossing WHERE favorite = :isFavorite")
    abstract fun loadFavoriteBorderCrossings(isFavorite: Boolean = true): LiveData<List<BorderCrossing>>

    @Query("UPDATE BorderCrossing SET favorite = :isFavorite WHERE crossingId = :crossingId")
    abstract fun updateFavorite(crossingId: Int, isFavorite: Boolean)

    @Transaction
    open fun updateBorderCrossings(crossings: List<BorderCrossing>) {
        markForRemoval()
        for (crossing in crossings) {
            updateBorderCrossings(
                crossing.crossingId,
                crossing.name,
                crossing.route,
                crossing.lane,
                crossing.direction,
                crossing.wait,
                crossing.updated,
                crossing.localCacheDate,
                false)
        }
        deleteOldCrossings()
        insertNewCrossings(crossings)
    }

    @Query("UPDATE BorderCrossing SET remove = 1")
    abstract fun markForRemoval()

    @Query("""
        UPDATE BorderCrossing SET
        name = :name,
        route = :route,
        lane = :lane,
        direction = :direction,
        wait = :wait,
        updated = :updated,
        localCacheDate = :localCacheDate
        remove = :remove
        WHERE crossingId = :crossingId
        """)
    abstract fun updateBorderCrossings(
        crossingId: Int,
        name: String,
        route: Int,
        lane: String,
        direction: String,
        wait: Int?,
        updated: Date?,
        localCacheDate: Date,
        remove: Boolean)

    @Query("DELETE FROM BorderCrossing WHERE remove = 1")
    abstract fun deleteOldCrossings()

}