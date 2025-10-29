package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SR167TollSignsFragment: TollSignsFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
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
        return "https://wsdot.wa.gov/travel/roads-bridges/toll-roads-bridges-tunnels/sr-167-express-toll-lanes"
    }
    override fun getInfoLinkText(): String {
        return getString(R.string.info_167)
    }
    override fun getRoute(): Int {
        return 167
    }
}