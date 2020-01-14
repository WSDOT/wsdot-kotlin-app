package gov.wa.wsdot.android.wsdot.ui.tollrates

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.SimpleTabFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.I405TollSignsFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.SR167TollSignsFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR16TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR520TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR99TollTableFragment

class TollRatesFragment: SimpleTabFragment(), Injectable {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).disableAds()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tolling, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_good_to_go -> {
                val action = NavGraphDirections.actionGlobalNavWebViewFragment("https://mygoodtogo.com/olcsc/", "My Good To Go")
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

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