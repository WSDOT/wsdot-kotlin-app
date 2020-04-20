package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateRow
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollTrip
import java.util.*

object TollRatesTestUtil {

    fun createTollSign(
        id: String = "",
        route: Int = 0,
        direction: String = ""
    ) = TollSign(
        id = id,
        locationName = "",
        stateRoute = route,
        milepost = 0f,
        travelDirection = direction,
        startLatitude = 0.0,
        startLongitude = 0.0,
        trips = mutableListOf<TollTrip>(),
        localCacheDate = Date(),
        favorite = false,
        remove = false
    )

    fun createTollRateTable(
        route: Int = 0
    ) = TollRateTable(
        route = route,
        message = "",
        numCol = 0,
        rows = mutableListOf(),
        localCacheDate = Date()
    )

}