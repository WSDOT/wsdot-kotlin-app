package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation
interface TravelerInfoMenuEventListener {

    enum class TravelerMenuItemType(val id: Int) {
        TRAVEL_TIMES(0),
        NEWS_ITEMS(1),
    }

    fun travelerInfoMenuEvent(eventType: TravelerMenuItemType)
}