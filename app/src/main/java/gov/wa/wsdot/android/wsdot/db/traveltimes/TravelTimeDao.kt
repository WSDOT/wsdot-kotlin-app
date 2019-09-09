package gov.wa.wsdot.android.wsdot.db.traveltimes

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
abstract class TravelTimeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewTravelTimes(times: List<TravelTime>)

    @Query("SELECT * FROM TravelTime")
    abstract fun loadTravelTimes(): LiveData<List<TravelTime>>

    @Query("SELECT * FROM TravelTime WHERE title LIKE ('%'||:query||'%')")
    abstract fun loadTravelTimesWithQueryText(query: String): LiveData<List<TravelTime>>

    @Query("SELECT * FROM TravelTime WHERE travelTimeId = :travelTimeId")
    abstract fun loadTravelTime(travelTimeId: Int): LiveData<TravelTime>

    @Query("SELECT * FROM TravelTime WHERE favorite = :isFavorite")
    abstract fun loadFavoriteTravelTimes(isFavorite: Boolean = true): LiveData<List<TravelTime>>

    @Query("UPDATE TravelTime SET favorite = :isFavorite WHERE travelTimeId = :travelTimeId")
    abstract fun updateFavorite(travelTimeId: Int, isFavorite: Boolean)

    @Transaction
    open fun updateTravelTimes(travelTimes: List<TravelTime>) {
        markTravelTimesForRemoval()
        for (travelTime in travelTimes) {
            updateTravelTime(
                travelTime.travelTimeId,
                travelTime.title,
                travelTime.via,
                travelTime.status,
                travelTime.avgTime,
                travelTime.currentTime,
                travelTime.miles,
                travelTime.startLocationLatitude,
                travelTime.startLocationLongitude,
                travelTime.endLocationLatitude,
                travelTime.endLocationLongitude,
                travelTime.updated,
                travelTime.localCacheDate,
                false)
        }
        deleteOldTravelTimes()
        insertNewTravelTimes(travelTimes)
    }

    @Query("UPDATE TravelTime SET remove = 1")
    abstract fun markTravelTimesForRemoval()

    @Query("""
        UPDATE TravelTime SET
        title = :title,
        via = :via,
        status = :status,
        avgTime = :avgTime,
        currentTime = :currentTime,
        miles = :miles,
        startLocationLatitude = :startLocationLatitude,
        startLocationLongitude = :startLocationLongitude,
        endLocationLatitude = :endLocationLatitude,
        endLocationLongitude = :endLocationLongitude,
        updated = :updated,
        localCacheDate = :localCacheDate,
        remove = :remove
        WHERE travelTimeId = :travelTimeId
    """)
    abstract fun updateTravelTime(
        travelTimeId: Int,
        title: String,
        via: String,
        status: String,
        avgTime: Int,
        currentTime: Int,
        miles: Float,
        startLocationLatitude: Double,
        startLocationLongitude: Double,
        endLocationLatitude: Double,
        endLocationLongitude: Double,
        updated: Date,
        localCacheDate: Date,
        remove: Boolean)

    @Query("DELETE FROM TravelTime WHERE remove = 1")
    abstract fun deleteOldTravelTimes()

}