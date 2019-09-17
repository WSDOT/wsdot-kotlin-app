package gov.wa.wsdot.android.wsdot.db.tollrates.dynamic

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class TollSignDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewTollSigns(tollSigns: List<TollSign>)

    @Query("SELECT * FROM TollSign WHERE stateRoute = :route AND travelDirection = :direction")
    abstract fun loadTollSignsOnRouteForDirection(route: Int, direction: String): LiveData<List<TollSign>>

    @Query("SELECT * FROM TollSign WHERE favorite = :isFavorite")
    abstract fun loadFavoriteTollSigns(isFavorite: Boolean = true): LiveData<List<TollSign>>

    @Query("UPDATE TollSign SET favorite = :isFavorite WHERE id = :id")
    abstract fun updateFavorite(id: Int, isFavorite: Boolean)

    @Transaction
    open fun updateTollSign(tollSigns: List<TollSign>) {
        markTollSignsForRemoval()
        for (tollSign in tollSigns) {
            updateTollSign(
                tollSign.id,
                tollSign.locationName,
                tollSign.stateRoute,
                tollSign.milepost,
                tollSign.travelDirection,
                tollSign.startLatitude,
                tollSign.startLongitude,
                tollSign.trips,
                tollSign.favorite,
                false)
        }
        deleteOldTollSigns()
        insertNewTollSigns(tollSigns)
    }

    @Query("UPDATE TollSign SET remove = 1")
    abstract fun markTollSignsForRemoval()

    @Query("""
        UPDATE TollSign SET
        locationName = :locationName,
        stateRoute = :stateRoute,
        milepost = :milepost,
        travelDirection = :travelDirection,
        startLatitude = :startLatitude,
        startLongitude = :startLongitude,
        trips = :trips,
        favorite = :favorite,
        remove = :remove
        WHERE id = :id
    """)
    abstract fun updateTollSign(
        id: Int,
        locationName: String,
        stateRoute: Int,
        milepost: Float,
        travelDirection: String,
        startLatitude: Double,
        startLongitude: Double,
        trips: List<TollTrip>,
        favorite: Boolean,
        remove: Boolean)

    @Query("DELETE FROM TollSign WHERE remove = 1")
    abstract fun deleteOldTollSigns()


}