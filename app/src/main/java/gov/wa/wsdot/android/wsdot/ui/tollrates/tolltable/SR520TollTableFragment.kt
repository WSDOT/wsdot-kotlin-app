package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SR520TollTableFragment: TollTableFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getRoute(): Int {
        return 520
    }

    override fun getInfoLinkURL(): String {
        return "https://wsdot.wa.gov/travel/roads-bridges/toll-roads-bridges-tunnels/sr-520-bridge-tolling"
    }
    override fun getInfoLinkText(): String {
        return getString(R.string.info_520)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tolling, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_good_to_go -> {
                val action = NavGraphDirections.actionGlobalNavWebViewFragment(
                    "https://mygoodtogo.com",
                    "My Good To Go"
                )
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}