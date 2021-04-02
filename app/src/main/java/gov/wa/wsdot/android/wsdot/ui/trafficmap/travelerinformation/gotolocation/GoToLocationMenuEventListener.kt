package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation.gotolocation
import gov.wa.wsdot.android.wsdot.model.eventItems.GoToLocationMenuEventItem
interface GoToLocationMenuEventListener {
    fun goToLocation(goToLocationItem: GoToLocationMenuEventItem)
}