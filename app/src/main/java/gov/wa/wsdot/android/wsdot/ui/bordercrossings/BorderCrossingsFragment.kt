package gov.wa.wsdot.android.wsdot.ui.bordercrossings

import androidx.fragment.app.Fragment
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes.NorthboundCrossingTimesFragment
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes.SouthboundCrossingTimesFragment
import gov.wa.wsdot.android.wsdot.ui.common.SimpleTabFragment

class BorderCrossingsFragment : SimpleTabFragment(), Injectable {

    // Only show northbound until further notice.
    // Accuracy of southbound times cannot be guaranteed indefinitely.
    override fun getTitles(): ArrayList<String> {
        val titles = ArrayList<String>()
        titles.add("Northbound")
        //titles.add("Southbound")
        return titles
    }

    override fun getFragments(): ArrayList<Fragment> {
        val fragments = ArrayList<Fragment>()
        fragments.add(NorthboundCrossingTimesFragment())
        //fragments.add(SouthboundCrossingTimesFragment())
        return fragments
    }
}