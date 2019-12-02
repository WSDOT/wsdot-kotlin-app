package gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts

import androidx.fragment.app.Fragment
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.SimpleTabFragment

class HighwayAlertTabFragment : SimpleTabFragment(), Injectable {
    override fun getTitles(): ArrayList<String> {
        val titles = ArrayList<String>()
        titles.add("Alerts In Area")
        titles.add("Highest Impact Alerts")
        return titles
    }
    override fun getFragments(): ArrayList<Fragment> {
        val fragments = ArrayList<Fragment>()
        fragments.add(MapHighwayAlertsFragment())
        fragments.add(HighestAlertsFragment())
        return fragments
    }
}