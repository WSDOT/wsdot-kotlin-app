package gov.wa.wsdot.android.wsdot.model.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime

class TravelTimeClusterItem : ClusterItem {
    private val mPosition: LatLng
    private var mTitle: String
    private var mSnippet: String

    var isSelected: Boolean = false
    val mTravelTime: TravelTime

    constructor(lat: Double, lng: Double, travelTime: TravelTime) {
        mPosition = LatLng(lat, lng)
        mTravelTime = travelTime
        mTitle = ""
        mSnippet = ""
    }

    constructor(lat: Double, lng: Double, travelTime: TravelTime, title: String, snippet: String) {
        mPosition = LatLng(lat, lng)
        mTravelTime = travelTime
        mTitle = title
        mSnippet = snippet
    }

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getSnippet(): String {
        return mSnippet
    }

}