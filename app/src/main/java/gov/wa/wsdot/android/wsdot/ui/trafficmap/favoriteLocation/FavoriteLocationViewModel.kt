package gov.wa.wsdot.android.wsdot.ui.trafficmap.favoriteLocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.FavoriteLocation
import gov.wa.wsdot.android.wsdot.repository.FavoriteLocationRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.util.*
import javax.inject.Inject

class FavoriteLocationViewModel @Inject constructor(favoriteLocationRepository: FavoriteLocationRepository) : ViewModel() {

    private val repo = favoriteLocationRepository
    var favoriteLocations : LiveData<Resource<List<FavoriteLocation>>> = favoriteLocationRepository.getFavoriteLocations()

    fun addFavoriteLocation(name: String, lat: Double, lng: Double, zoom: Float) {
        repo.addFavoriteLocation(name, lat, lng, zoom)
    }

    fun removeFavoriteLocation(creationDate: Date){
        repo.removeFavoriteLocation(creationDate)
    }

    fun removeAllFavoriteLocations(){
        repo.removeAllFavoriteLocations()
    }

}