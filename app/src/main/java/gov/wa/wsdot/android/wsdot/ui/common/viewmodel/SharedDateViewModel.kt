package gov.wa.wsdot.android.wsdot.ui.common.viewmodel

import java.util.*
import javax.inject.Inject

class SharedDateViewModel @Inject constructor() : SharedValueViewModel<Date>() {
    init {
        setValue(Calendar.getInstance().time)
    }
}