package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import gov.wa.wsdot.android.wsdot.R

class I405TollSignsFragment: TollSignsFragment() {
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