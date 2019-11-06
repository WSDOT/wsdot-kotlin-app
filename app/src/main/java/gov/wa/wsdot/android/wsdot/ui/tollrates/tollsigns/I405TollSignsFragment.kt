package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.os.Bundle
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class I405TollSignsFragment: TollSignsFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }
    override fun initTravelTimeIds(viewModel: TollSignsViewModel) {
        viewModel.setTravelTimeIds(
            35,
            36,
            38,
            37
        )
    }
    override fun getInfoLinkURL(): String {
        return "https://www.wsdot.com/Tolling/405/rates.htm"
    }
    override fun getInfoLinkText(): String {
        return getString(R.string.info_450)
    }
    override fun getRoute(): Int {
        return 405
    }
}