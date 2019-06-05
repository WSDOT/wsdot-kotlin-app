package gov.wa.wsdot.android.wsdot.ui.common.binding

import java.util.*

object BindingFunctions {

    @JvmStatic
    fun hasPassed(date: Date): Boolean {
        return date.before(Date())
    }

}