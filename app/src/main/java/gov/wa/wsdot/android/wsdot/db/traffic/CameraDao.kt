package gov.wa.wsdot.android.wsdot.db.traffic

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class CameraDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewCameras(cameras: List<Camera>)

    @Query("SELECT * FROM Camera")
    abstract fun loadCameras(): LiveData<List<Camera>>

    @Query("SELECT * FROM Camera WHERE favorite = :isFavorite  ORDER BY roadName, milepost ASC")
    abstract fun loadFavoriteCameras(isFavorite: Boolean = true): LiveData<List<Camera>>

    @Query("SELECT * FROM Camera WHERE cameraId = :cameraId")
    abstract fun loadCamera(cameraId: Int): LiveData<Camera>

    @Query("SELECT * FROM Camera WHERE roadName = :roadName")
    abstract fun loadCamerasOnRoad(roadName: String): LiveData<List<Camera>>

    @Query("SELECT * FROM Camera WHERE cameraId IN (:ids) ORDER BY milepost ASC")
    abstract fun loadCamerasWithIds(ids: List<Int>): LiveData<List<Camera>>

    @Query("SELECT * FROM Camera WHERE (latitude BETWEEN :minLat AND :maxLat) AND (longitude BETWEEN :minLng AND :maxLng)")
    abstract fun loadCamerasInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): LiveData<List<Camera>>

    @Query("UPDATE Camera SET favorite = :isFavorite WHERE cameraId = :cameraId")
    abstract fun updateFavorite(cameraId: Int, isFavorite: Boolean)

    @Transaction
    open fun updateCameras(cameras: List<Camera>) {
        markCamerasForRemoval()
        for (camera in cameras) {
            updateCamera(camera.cameraId, camera.title, camera.roadName, camera.milepost, camera.direction, camera.latitude, camera.longitude, camera.url, camera.hasVideo,false)
        }
        deleteOldCameras()
        insertNewCameras(cameras)
    }

    @Query("UPDATE Camera SET remove = 1")
    abstract fun markCamerasForRemoval()

    @Query("""
        UPDATE Camera SET
        title = :title,
        roadName = :roadName,
        milepost = :milepost,
        direction = :direction,
        latitude = :latitude,
        longitude = :longitude,
        url = :url,
        hasVideo = :hasVideo,
        remove = :remove
        WHERE cameraId = :cameraId
    """)
    abstract fun updateCamera(
        cameraId: Int,
        title: String,
        roadName: String,
        milepost: Int,
        direction: String?,
        latitude: Double,
        longitude: Double,
        url: String,
        hasVideo: Boolean,
        remove: Boolean)

    @Query("DELETE FROM Camera WHERE remove = 1")
    abstract fun deleteOldCameras()

}