package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation

import android.os.Parcelable

interface TravelerInfoMenuEventListener: Parcelable {
    fun travelerInfoMenuEvent(eventType: TravelerMenuItemType)
}