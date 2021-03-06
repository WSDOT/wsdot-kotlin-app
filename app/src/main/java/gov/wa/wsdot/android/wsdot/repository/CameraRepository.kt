package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.traffic.CamerasResponse
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traffic.CameraDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.util.*

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val cameraDao: CameraDao
) {

    fun loadCameras(forceRefresh: Boolean): LiveData<Resource<List<Camera>>> {

        return object : NetworkBoundResource<List<Camera>, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: List<Camera>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = cameraDao.loadCameras()

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadCamerasInBounds(bounds: LatLngBounds, forceRefresh: Boolean): LiveData<Resource<List<Camera>>> {

        return object : NetworkBoundResource<List<Camera>, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: List<Camera>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = cameraDao.loadCamerasInBounds(bounds.southwest.latitude, bounds.northeast.latitude, bounds.southwest.longitude, bounds.northeast.longitude)

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadCamerasOnRoadNorthOf(roadName: String, latitude: Double, forceRefresh: Boolean): LiveData<Resource<List<Camera>>> {

        return object : NetworkBoundResource<List<Camera>, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: List<Camera>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = cameraDao.loadCamerasOnRoadNorthOf(roadName, latitude)

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadCamera(cameraId: Int): LiveData<Resource<Camera>> {

        return object : NetworkBoundResource<Camera, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: Camera?): Boolean {

                var update = false

                if (data != null){
                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return update
            }

            override fun loadFromDb() = cameraDao.loadCamera(cameraId)

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadCamerasOnRoad(roadName: String, forceRefresh: Boolean): LiveData<Resource<List<Camera>>> {

        return object : NetworkBoundResource<List<Camera>, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: List<Camera>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = if (roadName == "") cameraDao.loadCameras() else cameraDao.loadCamerasOnRoad(roadName)

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadCamerasWithIds(cameraIds: List<Int>, forceRefresh: Boolean): LiveData<Resource<List<Camera>>> {

        return object : NetworkBoundResource<List<Camera>, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: List<Camera>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = cameraDao.loadCamerasWithIds(cameraIds)

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFavoriteCameras(forceRefresh: Boolean): LiveData<Resource<List<Camera>>> {

        return object : NetworkBoundResource<List<Camera>, CamerasResponse>(appExecutors) {

            override fun saveCallResult(item: CamerasResponse) = saveCameras(item)

            override fun shouldFetch(data: List<Camera>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = cameraDao.loadFavoriteCameras()

            override fun createCall() = dataWebservice.getCameras()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun updateFavorite(cameraId: Int, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            cameraDao.updateFavorite(cameraId, isFavorite)
        }
    }

    private fun saveCameras(camerasResponse: CamerasResponse) {

        var dbCameraList = arrayListOf<Camera>()

        for (cameraItem in camerasResponse.cameras.items) {

            val camera = Camera(
                cameraItem.cameraId,
                cameraItem.title,
                cameraItem.roadName,
                cameraItem.milepost,
                cameraItem.direction,
                cameraItem.latitude,
                cameraItem.longitude,
                cameraItem.url,
                (cameraItem.hasVideo != 0),
                Date(),
                favorite = false,
                remove = false
            )

            dbCameraList.add(camera)

        }
        cameraDao.updateCameras(dbCameraList)
    }
}
