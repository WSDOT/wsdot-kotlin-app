package gov.wa.wsdot.android.wsdot.ui.common.viewmodel

import java.util.*
import javax.inject.Inject

class SharedDateViewModel @Inject constructor() : SharedValueViewModel<Date>() {




    init {
        // set to current date on start
        setValue(Calendar.getInstance().time)
    }

}