package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import gov.wa.wsdot.android.wsdot.R

class SR167TollSignsFragment: TollSignsFragment() {
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