package gov.wa.wsdot.android.wsdot.db.traffic

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
abstract class FavoriteLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewFavoriteLocation(location: FavoriteLocation)

    @Query("SELECT * FROM FavoriteLocation")
    abstract fun loadFavoriteLocations(): LiveData<List<FavoriteLocation>>

    @Query("UPDATE FavoriteLocation SET title = :title WHERE creationDate = :creationDate")
    abstract fun updateFavoriteLocationTitle(creationDate: Date, title: String)

    @Query("DELETE FROM FavoriteLocation WHERE creationDate = :creationDate")
    abstract fun deleteFavoriteLocation(creationDate: Date)

    @Query("DELETE FROM FavoriteLocation")
    abstract fun deleteAllFavoriteLocations()

}