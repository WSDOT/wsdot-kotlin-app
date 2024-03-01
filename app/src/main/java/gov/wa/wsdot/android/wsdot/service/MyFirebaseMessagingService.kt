package gov.wa.wsdot.android.wsdot.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.service.helpers.MyNotificationManager
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.Utils

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {

            remoteMessage.data["push_alert_id"]?.toInt()?.let { alertId ->

                val settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val receivedAlerts = Utils.loadOrderedIntList("KEY_RECEIVED_ALERTS", settings)

                val title = remoteMessage.data["title"]
                val message = remoteMessage.data["message"]
                val type = remoteMessage.data["type"]

                if (!receivedAlerts.contains(alertId)) {
                    if (title != null && message != null && type != null) {
                        sendNotification(
                            alertId,
                            title,
                            message,
                            type,
                            getNotificationIntent(remoteMessage.data)
                        )
                    }
                }

                Thread(Runnable {
                    try {
                        cacheAlerts(alertId)
                    } catch (e: Exception) {
                        Log.e(TAG, "error with notification cache thread")
                    }
                }).start()

            }
        }
    }

    private fun getNotificationIntent(data: MutableMap<String, String>): PendingIntent {

        val type = data["type"]
        val alertId = data["alert_id"]
        val alertTitle = data["title"]

        val intent = Intent(this, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        when (type) {
            "highway_alert" -> {

                val lat = data["lat"]
                val lng = data["long"]

                intent.putExtra(getString(R.string.push_alert_traffic_alert), true)
                intent.putExtra(getString(R.string.push_alert_traffic_alert_id), alertId?.toInt())
                intent.putExtra(getString(R.string.push_alert_traffic_alert_title), alertTitle.toString())
                intent.putExtra(getString(R.string.push_alert_traffic_alert_latitude), lat?.toDouble())
                intent.putExtra(getString(R.string.push_alert_traffic_alert_longitude), lng?.toDouble())

            }
            "ferry_alert" -> {

                val routeId = data["route_id"]
                val routeTitle = data["route_title"]

                intent.putExtra(getString(R.string.push_alert_ferry_alert), true)
                intent.putExtra(getString(R.string.push_alert_ferry_alert_id), alertId?.toInt())
                intent.putExtra(getString(R.string.push_alert_ferry_route_id), routeId?.toInt())
                intent.putExtra(getString(R.string.push_alert_ferry_route_title), routeTitle.toString())

            }
            "bridge_alert" -> {

                val lat = data["lat"]
                val lng = data["long"]
                val title = data["title"]
                val message = data["message"]

                intent.putExtra(getString(R.string.push_alert_bridge_alert), true)
                intent.putExtra(getString(R.string.push_alert_bridge_alert_id), alertId?.toInt())
                intent.putExtra(getString(R.string.push_alert_bridge_alert_latitude), lat?.toDouble())
                intent.putExtra(getString(R.string.push_alert_bridge_alert_longitude), lng?.toDouble())
                intent.putExtra(getString(R.string.push_alert_bridge_alert_title), title)
                intent.putExtra(getString(R.string.push_alert_bridge_alert_message), message)

            }
        }
        return PendingIntent.getActivity(this, alertId?.toInt() ?: 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(notificationId: Int, title: String, messageBody: String, type: String, pendingIntent: PendingIntent) {

        var channelId = MyNotificationManager.ALERT_CHANNEL_ID

        when (type) {
            "highway_alert" -> {
                channelId = MyNotificationManager.ALERT_CHANNEL_ID
            }
            "ferry_alert" -> {
                channelId = MyNotificationManager.ALERT_CHANNEL_ID
            }
            "bridge_alert" -> {
                channelId = MyNotificationManager.BRIDGE_CHANNEL_ID
            }
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_list_wsdot)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody))
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun cacheAlerts(alertId: Int){
        if (alertId != 0) {
            val settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val receivedAlerts = Utils.loadOrderedIntList("KEY_RECEIVED_ALERTS", settings)
            receivedAlerts.add(alertId)
            Utils.saveOrderedIntList(
                receivedAlerts,
                "KEY_RECEIVED_ALERTS",
                settings
            )
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}