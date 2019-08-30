package gov.wa.wsdot.android.wsdot.ui.favorites

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.repository.TravelTimesRepository
import gov.wa.wsdot.android.wsdot.ui.favorites.items.CameraFavorites
import gov.wa.wsdot.android.wsdot.ui.favorites.items.FavoriteItems
import gov.wa.wsdot.android.wsdot.ui.favorites.items.TravelTimeFavorite
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class FavoritesListViewModel @Inject constructor(
    cameraRepository: CameraRepository,
    travelTimesRepository: TravelTimesRepository
) : ViewModel() {

    private val travelTimesRepo = travelTimesRepository
    private val cameraRepo = cameraRepository

    // mediator handles resubscribe on refresh
    val favorites = MediatorLiveData<FavoriteItems>()

    private var travelTimeLiveData : LiveData<Resource<List<TravelTime>>> = travelTimesRepository.loadTravelTimes(false)
    private var cameraLiveData : LiveData<Resource<List<Camera>>> = cameraRepository.loadCameras(false)

    init {
        favorites.addSource(travelTimeLiveData) {
            it?.let {
                favorites.value = TravelTimeFavorite(it)
            }
        }

        favorites.addSource(cameraLiveData) {
            it?.let {
                favorites.value = CameraFavorites(it)
            }
        }
    }

    fun refresh() {

        favorites.removeSource(travelTimeLiveData)
        favorites.removeSource(cameraLiveData)

        travelTimeLiveData = travelTimesRepo.loadTravelTimes(true)
        cameraLiveData = cameraRepo.loadCameras(true)

        favorites.addSource(travelTimeLiveData) {
            it?.let {
                favorites.value = TravelTimeFavorite(it)
            }
        }
        favorites.addSource(cameraLiveData) {
            it?.let {
                favorites.value = CameraFavorites(it)
            }
        }
    }
}