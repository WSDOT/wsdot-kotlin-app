package gov.wa.wsdot.android.wsdot.util

import android.content.Context
import android.content.res.Configuration

object NightModeConfig {
    fun nightModeOn(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}