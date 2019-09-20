package gov.wa.wsdot.android.wsdot.ui.favorites

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traffic.FavoriteLocation
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.repository.*
import gov.wa.wsdot.android.wsdot.ui.favorites.items.*
import gov.wa.wsdot.android.wsdot.util.network.DBResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject

class FavoritesListViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val travelTimesRepository: TravelTimesRepository,
    private val ferriesRepository: FerriesRepository,
    private val mountainPassRepository: MountainPassRepository,
    private val borderCrossingRepository: BorderCrossingRepository,
    private val favoriteLocationRepository: FavoriteLocationRepository,
    private val tollSignRepository: TollSignRepository
) : ViewModel() {

    // mediator handles refresh status. Used by data bound refresh control in layout xml
    val favoritesLoadingStatus = MediatorLiveData<Resource<FavoriteItems>>()

    // mediators handle each favorite item list
    val favoriteTravelTimes = MediatorLiveData<List<TravelTime>>()
    val favoriteFerrySchedules = MediatorLiveData<List<FerrySchedule>>()
    val favoriteCameras = MediatorLiveData<List<Camera>>()
    val favoriteMountainPasses = MediatorLiveData<List<MountainPass>>()
    val favoriteBorderCrossings = MediatorLiveData<List<BorderCrossing>>()
    val favoriteLocations = MediatorLiveData<List<FavoriteLocation>>()
    val favoriteTollSigns = MediatorLiveData<List<TollSign>>()

    private var travelTimeLiveData : LiveData<Resource<List<TravelTime>>> = travelTimesRepository.loadFavoriteTravelTimes(false)
    private var cameraLiveData : LiveData<Resource<List<Camera>>> = cameraRepository.loadFavoriteCameras(false)
    private var ferryScheduleLiveData: LiveData<Resource<List<FerrySchedule>>> = ferriesRepository.loadFavoriteSchedules(false)
    private var mountainPassLiveData: LiveData<Resource<List<MountainPass>>> = mountainPassRepository.loadFavoritePasses(false)
    private var borderCrossingLiveData: LiveData<Resource<List<BorderCrossing>>> = borderCrossingRepository.loadFavoriteCrossings(false)
    private var favoriteLocationLiveDate: LiveData<Resource<List<FavoriteLocation>>> = favoriteLocationRepository.getFavoriteLocations()
    private var tollSignLiveData: LiveData<Resource<List<TollSign>>> = tollSignRepository.loadFavoriteTollSign(false)

    init {
        addSources()
        favoriteLocations.addSource(favoriteLocationLiveDate) {
            it?.data?.let { favItems ->
                favoriteLocations.value = favItems
            }
        }
    }

    fun updateFavoriteFerrySchedule(routeId: Int, isFavorite: Boolean) {
        ferriesRepository.updateFavorite(routeId, isFavorite)
    }

    fun updateFavoriteTravelTime(travelTimeId: Int, isFavorite: Boolean) {
        travelTimesRepository.updateFavorite(travelTimeId, isFavorite)
    }

    fun updateFavoriteCamera(cameraId: Int, isFavorite: Boolean) {
        cameraRepository.updateFavorite(cameraId, isFavorite)
    }

    fun updateFavoritePass(passId: Int, isFavorite: Boolean) {
        mountainPassRepository.updateFavorite(passId, isFavorite)
    }

    fun updateFavoriteBorderCrossings(crossingId: Int, isFavorite: Boolean) {
        borderCrossingRepository.updateFavorite(crossingId, isFavorite)
    }

    fun removeFavoriteLocation(location: FavoriteLocation){
        favoriteLocationRepository.removeFavoriteLocation(location.creationDate)
    }

    fun updateFavoriteTollSign(tollSignId: String, isFavorite: Boolean){
        tollSignRepository.updateFavorite(tollSignId, isFavorite)
    }

    fun addFavoriteLocation(location: FavoriteLocation) {
        favoriteLocationRepository.addFavoriteLocation(
            location.title,
            location.latitude,
            location.longitude,
            location.zoom
        )
    }

    fun refresh() {
        removeSources()
        travelTimeLiveData = travelTimesRepository.loadFavoriteTravelTimes(true)
        cameraLiveData = cameraRepository.loadFavoriteCameras(true)
        ferryScheduleLiveData = ferriesRepository.loadFavoriteSchedules(true)
        mountainPassLiveData = mountainPassRepository.loadFavoritePasses(true)
        borderCrossingLiveData = borderCrossingRepository.loadFavoriteCrossings(true)
        tollSignLiveData = tollSignRepository.loadFavoriteTollSign(true)
        addSources()
    }

    private fun addSources() {

        // Travel Times
        favoriteTravelTimes.addSource(travelTimeLiveData) {
            it?.data?.let { favItems ->
                favoriteTravelTimes.value = favItems
            }
        }

        favoritesLoadingStatus.addSource(travelTimeLiveData) {
            it?.let {
                it.data?.let { data ->
                    when (it.status) {
                        Status.SUCCESS -> favoritesLoadingStatus.value = Resource.success(TravelTimeData(data))
                        Status.LOADING -> favoritesLoadingStatus.value = Resource.loading(TravelTimeData(data))
                        Status.ERROR -> favoritesLoadingStatus.value =
                            Resource.error(it.message!!, TravelTimeData(data))
                    }
                }
            }
        }

        // Cameras
        favoriteCameras.addSource(cameraLiveData) {
            it?.data?.let { favItems ->
                favoriteCameras.value = favItems
            }
        }

        favoritesLoadingStatus.addSource(cameraLiveData) {
            it?.let {
                it.data?.let { data ->
                    when (it.status) {
                        Status.SUCCESS -> favoritesLoadingStatus.value = Resource.success(CameraData(data))
                        Status.LOADING -> favoritesLoadingStatus.value = Resource.loading(CameraData(data))
                        Status.ERROR -> favoritesLoadingStatus.value =
                            Resource.error(it.message!!, CameraData(data))
                    }
                }
            }
        }

        // Ferry Schedules
        favoriteFerrySchedules.addSource(ferryScheduleLiveData) {
            it?.data?.let { favItems ->
                favoriteFerrySchedules.value = favItems
            }
        }

        favoritesLoadingStatus.addSource(ferryScheduleLiveData) {
            it?.let {
                it.data?.let { data ->
                    when (it.status) {
                        Status.SUCCESS -> favoritesLoadingStatus.value = Resource.success(FerryScheduleData(data))
                        Status.LOADING -> favoritesLoadingStatus.value = Resource.loading(FerryScheduleData(data))
                        Status.ERROR -> favoritesLoadingStatus.value =
                            Resource.error(it.message!!, FerryScheduleData(data))
                    }
                }
            }
        }

        // Mountain Passes
        favoriteMountainPasses.addSource(mountainPassLiveData) {
            it?.data?.let { favItems ->
                favoriteMountainPasses.value = favItems
            }
        }

        favoritesLoadingStatus.addSource(mountainPassLiveData) {
            it?.let {
                it.data?.let { data ->
                    when (it.status) {
                        Status.SUCCESS -> favoritesLoadingStatus.value = Resource.success(MountainPassData(data))
                        Status.LOADING -> favoritesLoadingStatus.value = Resource.loading(MountainPassData(data))
                        Status.ERROR -> favoritesLoadingStatus.value =
                            Resource.error(it.message!!, MountainPassData(data))
                    }
                }
            }
        }

        // Border Crossings
        favoriteBorderCrossings.addSource(borderCrossingLiveData) {
            it?.data?.let { favItems ->
                favoriteBorderCrossings.value = favItems
            }
        }

        favoritesLoadingStatus.addSource(borderCrossingLiveData) {
            it?.let {
                it.data?.let { data ->
                    when (it.status) {
                        Status.SUCCESS -> favoritesLoadingStatus.value = Resource.success(BorderCrossingData(data))
                        Status.LOADING -> favoritesLoadingStatus.value = Resource.loading(BorderCrossingData(data))
                        Status.ERROR -> favoritesLoadingStatus.value =
                            Resource.error(it.message!!, BorderCrossingData(data))
                    }
                }
            }
        }

        // Border Crossings
        favoriteTollSigns.addSource(tollSignLiveData) {
            it?.data.let {favItems ->
                favoriteTollSigns.value = favItems
            }
        }

        favoritesLoadingStatus.addSource(tollSignLiveData) {
            it?.let {
                it.data?.let { data ->
                    when (it.status) {
                        Status.SUCCESS -> favoritesLoadingStatus.value = Resource.success(TollSignData(data))
                        Status.LOADING -> favoritesLoadingStatus.value = Resource.loading(TollSignData(data))
                        Status.ERROR -> favoritesLoadingStatus.value =
                            Resource.error(it.message!!, TollSignData(data))
                    }
                }
            }
        }

    }

    private fun removeSources() {
        favoriteTravelTimes.removeSource(travelTimeLiveData)
        favoriteCameras.removeSource(cameraLiveData)
        favoriteFerrySchedules.removeSource(ferryScheduleLiveData)
        favoriteMountainPasses.removeSource(mountainPassLiveData)
        favoriteBorderCrossings.removeSource(borderCrossingLiveData)
        favoriteTollSigns.removeSource(tollSignLiveData)

        favoritesLoadingStatus.removeSource(travelTimeLiveData)
        favoritesLoadingStatus.removeSource(cameraLiveData)
        favoritesLoadingStatus.removeSource(ferryScheduleLiveData)
        favoritesLoadingStatus.removeSource(mountainPassLiveData)
        favoritesLoadingStatus.removeSource(borderCrossingLiveData)
        favoritesLoadingStatus.removeSource(tollSignLiveData)
    }
}