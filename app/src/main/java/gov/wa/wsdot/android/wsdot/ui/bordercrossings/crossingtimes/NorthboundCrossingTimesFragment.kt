package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

class NorthboundCrossingTimesFragment : CrossingTimesFragment() {
    override fun getDirection(): String {
        return "northbound"
    }
}