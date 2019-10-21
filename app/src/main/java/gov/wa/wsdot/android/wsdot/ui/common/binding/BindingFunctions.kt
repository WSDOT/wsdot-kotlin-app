package gov.wa.wsdot.android.wsdot.ui.common.binding

import java.util.*

object BindingFunctions {

    @JvmStatic
    fun hasPassed(date: Date?): Boolean {
        date?.let { return it.before(Date()) }
        return false
    }

}