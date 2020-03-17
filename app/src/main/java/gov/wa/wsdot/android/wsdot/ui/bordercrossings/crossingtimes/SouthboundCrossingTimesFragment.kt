package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.ui.MainActivity

/**
 * Class inherits from the BaseCrossingTimeFragment and sets the
 * direction string. this string is passed to the view model to
 * query for the correct wait times
 *
 * Southbound times are currently disabled due to reliability issues.
 */
class SouthboundCrossingTimesFragment : BaseCrossingTimesFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }
    override fun getDirection(): String {
        return "southbound"
    }
}