package gov.wa.wsdot.android.wsdot.util

import android.util.SparseArray
import com.google.android.gms.maps.model.LatLng
import gov.wa.wsdot.android.wsdot.model.FerriesTerminalItem
import java.lang.Math.pow
import kotlin.math.*

object DistanceUtils {

    /**
     * Haversine formula
     *
     * Provides great-circle distances between two points on a sphere from their longitudes and latitudes
     *
     * http://en.wikipedia.org/wiki/Haversine_formula
     *
     */
    fun getDistanceFromPoints(latitudeA: Double, longitudeA: Double, latitudeB: Double, longitudeB: Double): Int {
        val earthRadius = 3958.75 // miles
        val dLat = Math.toRadians(latitudeA - latitudeB)
        val dLng = Math.toRadians(longitudeA - longitudeB)
        val sindLat = sin(dLat / 2)
        val sindLng = sin(dLng / 2)
        val a = pow(sindLat, 2.0) + (pow(sindLng, 2.0)
                * cos(Math.toRadians(latitudeB))
                * cos(Math.toRadians(latitudeA)))

        val c = 2 * asin(sqrt(a))
        return (earthRadius * c).roundToInt()

    }

    fun getCenterLocation(
        latitudeA: Double,
        longitudeA: Double,
        latitudeB: Double,
        longitudeB: Double
    ): LatLng {
        var latA = latitudeA
        var longA = longitudeA
        var latB = latitudeB

        val dLon = Math.toRadians(longitudeB - longA)

        latA = Math.toRadians(latA)
        latB = Math.toRadians(latB)
        longA = Math.toRadians(longA)

        val Bx = cos(latB) * cos(dLon)
        val By = cos(latB) * sin(dLon)
        val latitudeC = atan2(
            sin(latA) + sin(latB),
            sqrt((cos(latA) + Bx) * (cos(latA) + Bx) + By * By)
        )
        val longitudeC = longA + atan2(By, cos(latA) + Bx)

        return LatLng(Math.toDegrees(latitudeC), Math.toDegrees(longitudeC))

    }




    fun getTerminalLocations(): SparseArray<FerriesTerminalItem> {

        val ferriesTerminalMap = SparseArray<FerriesTerminalItem>()

        ferriesTerminalMap.put(1, FerriesTerminalItem(1, "Anacortes", 48.507351, -122.677))
        ferriesTerminalMap.put(3, FerriesTerminalItem(3, "Bainbridge Island", 47.622339, -122.509617))
        ferriesTerminalMap.put(4, FerriesTerminalItem(4, "Bremerton", 47.561847, -122.624089))
        ferriesTerminalMap.put(5, FerriesTerminalItem(5, "Clinton", 47.9754, -122.349581))
        ferriesTerminalMap.put(11, FerriesTerminalItem(11, "Coupeville", 48.159008, -122.672603))
        ferriesTerminalMap.put(8, FerriesTerminalItem(8, "Edmonds", 47.813378, -122.385378))
        ferriesTerminalMap.put(9, FerriesTerminalItem(9, "Fauntleroy", 47.5232, -122.3967))
        ferriesTerminalMap.put(10, FerriesTerminalItem(10, "Friday Harbor", 48.535783, -123.013844))
        ferriesTerminalMap.put(12, FerriesTerminalItem(12, "Kingston", 47.794606, -122.494328))
        ferriesTerminalMap.put(13, FerriesTerminalItem(13, "Lopez Island", 48.570928, -122.882764))
        ferriesTerminalMap.put(14, FerriesTerminalItem(14, "Mukilteo", 47.9506, -122.297))
        ferriesTerminalMap.put(15, FerriesTerminalItem(15, "Orcas Island", 48.597333, -122.943494))
        ferriesTerminalMap.put(16, FerriesTerminalItem(16, "Point Defiance", 47.306519, -122.514053))
        ferriesTerminalMap.put(17, FerriesTerminalItem(17, "Port Townsend", 48.110847, -122.759039))
        ferriesTerminalMap.put(7, FerriesTerminalItem(7, "Seattle", 47.602501, -122.340472))
        ferriesTerminalMap.put(18, FerriesTerminalItem(18, "Shaw Island", 48.584792, -122.92965))
        ferriesTerminalMap.put(19, FerriesTerminalItem(19, "Sidney B.C.", 48.643114, -123.396739))
        ferriesTerminalMap.put(20, FerriesTerminalItem(20, "Southworth", 47.513064, -122.495742))
        ferriesTerminalMap.put(21, FerriesTerminalItem(21, "Tahlequah", 47.331961, -122.507786))
        ferriesTerminalMap.put(22, FerriesTerminalItem(22, "Vashon Island", 47.51095, -122.463639))

        return ferriesTerminalMap
    }

    fun getTerminals(): List<LatLng> {

        val ferriesTerminals = listOf(
            LatLng(48.507351, -122.677), // Anacortes
            LatLng(47.622339, -122.509617), // Bainbridge Island
            LatLng(47.561847, -122.624089), // Bremerton
            LatLng(47.9754, -122.349581), // Clinton
            LatLng(48.159008, -122.672603), // Coupeville
            LatLng(47.813378, -122.385378), // Edmonds
            LatLng(47.5232, -122.3967), // Fauntleroy
            LatLng(48.535783, -123.013844), // Friday Harbor
            LatLng(47.794606, -122.494328), // Kingston
            LatLng(48.570928, -122.882764), // Lopez Island
            LatLng(47.949544, -122.304997), // Mukilteo
            LatLng(48.597333, -122.943494), // Orcas Island
            LatLng(47.306519, -122.514053), // Point Defiance
            LatLng(48.110847, -122.759039), // Port Townsend
            LatLng(47.602501, -122.340472), // Seattle
            LatLng(48.584792, -122.92965), // Shaw Island
            LatLng(48.643114, -123.396739), // Sidney B.C.
            LatLng(47.513064, -122.495742), // Southworth
            LatLng(47.331961, -122.507786), // Tahlequah
            LatLng(47.51095, -122.463639) // Vashon Island
        )

        return ferriesTerminals
    }

}