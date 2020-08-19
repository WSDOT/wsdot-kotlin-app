package gov.wa.wsdot.android.wsdot.util

object AdTargets {
    fun getFerryAdTarget(routeId: Int?): String {
        return when (routeId) {
            9 -> "Anacortes-SanJuan"
            272 -> "Anacortes-SanJuan" // "Anacortes / San Juan Islands / Sidney B.C."
            6 -> "Edmonds-Kingston"
            13 -> "Fauntleroy-Southworth"
            14 -> "Fauntleroy-Vashon"
            7 -> "Mukilteo-Clinton"
            8 -> "PortTownsend-Couperville"
            5 -> "Seattle-Bainbridge"
            3 -> "Seattle-Bremerton"
            15 -> "Southworth-Vashon"
            1 -> "PtDefiance-Tahlequah"
            else -> {
                "ferries-home"
            }
        }
    }
}