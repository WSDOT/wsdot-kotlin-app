package gov.wa.wsdot.android.wsdot.model.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import gov.wa.wsdot.android.wsdot.db.traffic.Camera

class CameraClusterItem : ClusterItem {
    private val mPosition: LatLng
    private var mTitle: String
    private var mSnippet: String

    val mCamera: Camera

    constructor(lat: Double, lng: Double, camera: Camera) {
        mPosition = LatLng(lat, lng)
        mCamera = camera
        mTitle = ""
        mSnippet = ""
    }

    constructor(lat: Double, lng: Double, camera: Camera, title: String, snippet: String) {
        mPosition = LatLng(lat, lng)
        mCamera = camera
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