package gov.wa.wsdot.android.wsdot.service.helpers

import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

class MyNotificationManager(private val context: Context) {

    val mainNotificationId: String
        get() = ALERT_CHANNEL_ID

    @RequiresApi(Build.VERSION_CODES.O)
    fun createMainNotificationChannels() {
        createAlertsChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAlertsChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val mChannel = NotificationChannel(ALERT_CHANNEL_ID, ALERT_CHANNEL_NAME, importance)
        mChannel.description = ALERT_CHANNEL_DESCRIPTION
        mChannel.lightColor = Color.GREEN
        context.getSystemService(Context.NOTIFICATION_SERVICE)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        var ALERT_CHANNEL_ID = "ALERTS"
        private const val ALERT_CHANNEL_NAME = "Alerts"
        private const val ALERT_CHANNEL_DESCRIPTION = "Notifications from WSDOT"
    }

}