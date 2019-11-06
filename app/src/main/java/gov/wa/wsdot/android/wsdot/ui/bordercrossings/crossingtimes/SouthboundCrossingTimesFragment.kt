package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SouthboundCrossingTimesFragment : BaseCrossingTimesFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }
    override fun getDirection(): String {
        return "southbound"
    }
}