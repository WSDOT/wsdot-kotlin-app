package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface TravelerInfoMenuEventListener: Parcelable {
    fun travelerInfoMenuEvent(eventType: TravelerMenuItemType)
}