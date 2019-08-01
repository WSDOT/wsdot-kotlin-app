package gov.wa.wsdot.android.wsdot.model

class RestAreaItem (
    var route: String,
    var description: String,
    var location: String,
    var milepost: Int,
    var direction: String,
    var latitude: Double,
    var longitude: Double,
    var icon: Int,
    var notes: String,
    var amenities: ArrayList<String>
)
