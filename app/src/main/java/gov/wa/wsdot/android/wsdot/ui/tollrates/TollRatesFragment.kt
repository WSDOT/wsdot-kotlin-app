package gov.wa.wsdot.android.wsdot.ui.tollrates

import androidx.fragment.app.Fragment
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.SimpleTabFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.I405TollSignsFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.SR167TollSignsFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR16TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR520TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR99TollTableFragment

class TollRatesFragment: SimpleTabFragment(), Injectable {

    override fun getFragments(): ArrayList<Fragment>{
        val fragments = ArrayList<Fragment>()
        fragments.add(SR16TollTableFragment())
        fragments.add(SR520TollTableFragment())
        fragments.add(SR99TollTableFragment())
        fragments.add(SR167TollSignsFragment())
        fragments.add(I405TollSignsFragment())
        return fragments
    }

    override fun getTitles(): ArrayList<String> {
        val titles = ArrayList<String>()
        titles.add("SR 16")
        titles.add("SR 520")
        titles.add("SR 99")
        titles.add("SR 167")
        titles.add("I-405")
        return titles
    }

}