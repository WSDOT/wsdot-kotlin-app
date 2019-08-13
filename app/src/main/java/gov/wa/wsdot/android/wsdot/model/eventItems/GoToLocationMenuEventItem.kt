package gov.wa.wsdot.android.wsdot.model.eventItems

import com.google.android.gms.maps.model.LatLng

class GoToLocationMenuEventItem (
    var name: String,
    var location: LatLng,
    var zoom: Float
)