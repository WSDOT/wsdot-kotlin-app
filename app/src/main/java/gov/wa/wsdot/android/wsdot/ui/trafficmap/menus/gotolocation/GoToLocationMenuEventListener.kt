package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.gotolocation
import gov.wa.wsdot.android.wsdot.model.eventItems.GoToLocationMenuEventItem
interface GoToLocationMenuEventListener {
    fun goToLocation(goToLocationItem: GoToLocationMenuEventItem)
}