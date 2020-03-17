package gov.wa.wsdot.android.wsdot.ui.amtrakcascades.helpers

import com.google.android.gms.maps.model.LatLng

/**
 *  Helper class that holds lat/longs for amtrak stations.
 */
object AmtrakUtils {

    fun getStationLocations(): Map<String, LatLng> {

        return mapOf(
            Pair("VAC", LatLng(49.2737293, -123.0979175)),
            Pair("BEL", LatLng(48.720423, -122.5109386)),
            Pair("MVW", LatLng(48.4185923, -122.334973)),
            Pair("STW", LatLng(48.2417732, -122.3495322)),
            Pair("EVR", LatLng(47.975512, -122.197854)),
            Pair("EDM", LatLng(47.8111305, -122.3841639)),
            Pair("SEA", LatLng(47.6001899, -122.3314322)),
            Pair("TUK", LatLng(47.461079, -122.242693)),
            Pair("TAC", LatLng(47.2419939, -122.4205623)),
            Pair("OLW", LatLng(46.9913576, -122.793982)),
            Pair("CTL", LatLng(46.7177596, -122.9528291)),
            Pair("KEL", LatLng(46.1422504, -122.9132438)),
            Pair("VAN", LatLng(45.6294472, -122.685568)),
            Pair("PDX", LatLng(45.528639, -122.676284)),
            Pair("ORC", LatLng(45.3659422, -122.5960671)),
            Pair("SLM", LatLng(44.9323665, -123.0281591)),
            Pair("ALY", LatLng(44.6300975, -123.1041787)),
            Pair("EUG", LatLng(44.055506, -123.094523))
        )


    }

}