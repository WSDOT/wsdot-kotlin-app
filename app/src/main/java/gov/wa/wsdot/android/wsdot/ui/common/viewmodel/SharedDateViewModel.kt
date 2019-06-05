package gov.wa.wsdot.android.wsdot.ui.common.viewmodel

import java.util.*
import javax.inject.Inject

// ViewModel lets us share the set data from the daypicker fragment with any fragment that wants it
class SharedDateViewModel @Inject constructor() : SharedValueViewModel<Date>() {

    init {
        // set to current date on start
        setValue(Calendar.getInstance().time)
    }

}