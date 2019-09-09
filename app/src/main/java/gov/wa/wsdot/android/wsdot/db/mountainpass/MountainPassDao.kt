package gov.wa.wsdot.android.wsdot.db.mountainpass

import androidx.lifecycle.LiveData
import androidx.room.*
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassCamera
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassForecast
import java.util.*

@Dao
abstract class MountainPassDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewPasses(passes: List<MountainPass>)

    @Query("SELECT * FROM MountainPass ORDER BY MountainPass.passName ASC")
    abstract fun loadPasses(): LiveData<List<MountainPass>>

    @Query("SELECT * FROM MountainPass WHERE favorite = :isFavorite ORDER BY MountainPass.passName ASC")
    abstract fun loadFavoritePasses(isFavorite: Boolean = true): LiveData<List<MountainPass>>

    @Query("SELECT * FROM MountainPass WHERE passId = (:passId)")
    abstract fun loadPass(passId: Int): LiveData<MountainPass>

    @Query("UPDATE MountainPass SET favorite = :isFavorite WHERE passId = :passId")
    abstract fun updateFavorite(passId: Int, isFavorite: Boolean)

    @Transaction
    open fun updateMountainPasses(passes: List<MountainPass>) {
        markPassesForRemoval()
        for (pass in passes) {
            updatePasses(
                pass.passId,
                pass.passName,
                pass.roadCondition,
                pass.weatherCondition,
                pass.temperatureInFahrenheit,
                pass.elevationInFeet,
                pass.travelAdvisoryActive,
                pass.latitude,
                pass.longitude,
                pass.restrictionOneText,
                pass.restrictionOneDirection,
                pass.restrictionTwoText,
                pass.restrictionTwoDirection,
                pass.cameras,
                pass.forecasts,
                pass.serverCacheDate,
                pass.localCacheDate,
                false)
        }
        deleteOldPasses()
        insertNewPasses(passes)
    }

    @Query("UPDATE MountainPass SET remove = 1")
    abstract fun markPassesForRemoval()

    @Query("""
        UPDATE MountainPass SET
        passName = :passName,
        roadCondition = :roadCondition,
        weatherCondition = :weatherCondition,
        temperatureInFahrenheit = :temperatureInFahrenheit,
        elevationInFeet = :elevationInFeet,
        travelAdvisoryActive = :travelAdvisoryActive,
        latitude = :latitude,
        longitude = :longitude,
        restrictionOneText = :restrictionOneText,
        restrictionOneDirection = :restrictionOneDirection,
        restrictionTwoText = :restrictionTwoText,
        restrictionTwoDirection = :restrictionTwoDirection,
        cameras = :cameras,
        forecasts = :forecasts,
        serverCacheDate = :serverCacheDate,
        localCacheDate = :localCacheDate,
        remove = :remove
        WHERE passId = :passId
    """)
    abstract fun updatePasses(
        passId: Int,
        passName: String,
        roadCondition: String,
        weatherCondition: String,
        temperatureInFahrenheit: Int,
        elevationInFeet: Int,
        travelAdvisoryActive: Boolean,
        latitude: Double,
        longitude: Double,
        restrictionOneText: String,
        restrictionOneDirection: String,
        restrictionTwoText: String,
        restrictionTwoDirection: String,
        cameras: List<PassCamera>,
        forecasts: List<PassForecast>,
        serverCacheDate: Date,
        localCacheDate: Date,
        remove: Boolean)

    @Query("DELETE FROM MountainPass WHERE remove = 1")
    abstract fun deleteOldPasses()

}