package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SR16TollTableFragment: TollTableFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }
    override fun getRoute(): Int {
        return 16
    }
}