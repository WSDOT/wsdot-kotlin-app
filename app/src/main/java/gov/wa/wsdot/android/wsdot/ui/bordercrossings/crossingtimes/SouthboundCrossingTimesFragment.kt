package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SouthboundCrossingTimesFragment : BaseCrossingTimesFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }
    override fun getDirection(): String {
        return "southbound"
    }
}