package gov.wa.wsdot.android.wsdot.ui.trafficmap.gotolocation
import gov.wa.wsdot.android.wsdot.model.map.GoToLocationItem
interface GoToLocationEventListener {
    fun goToLocation(goToLocationItem: GoToLocationItem)
}