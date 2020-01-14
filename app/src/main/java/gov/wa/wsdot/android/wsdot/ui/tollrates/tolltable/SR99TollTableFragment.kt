package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SR99TollTableFragment: TollTableFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }
    override fun getRoute(): Int {
        return 99
    }
}