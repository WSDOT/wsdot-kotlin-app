package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SR167TollSignsFragment: TollSignsFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun initTravelTimeIds(viewModel: TollSignsViewModel) {
        viewModel.setTravelTimeIds(
            67,
            68,
            70,
            69
        )
    }

    override fun getInfoLinkURL(): String {
        return "https://www.wsdot.wa.gov/Tolling/SR167HotLanes/default.htm"
    }
    override fun getInfoLinkText(): String {
        return getString(R.string.info_167)
    }
    override fun getRoute(): Int {
        return 167
    }
}