package gov.wa.wsdot.android.wsdot

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import gov.wa.wsdot.android.wsdot.di.AppInjector
import gov.wa.wsdot.android.wsdot.service.helpers.MyNotificationManager
import javax.inject.Inject

class WsdotApp : Application(), HasActivityInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        setDarkMode(
            PreferenceManager.getDefaultSharedPreferences(this),
            getString(R.string.pref_key_user_theme)
        )
        super.onCreate()

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val myNotificationManager = MyNotificationManager(this)
            myNotificationManager.createMainNotificationChannels()
        }

        // reset driving message display pref
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = settings.edit()
        editor.putBoolean(getString(R.string.pref_key_has_seen_driving_message), false)
        editor.apply()

        AppInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    fun setDarkMode(sharedPreferences: SharedPreferences?, prefKey: String?) {
        sharedPreferences?.let { prefs ->
            if (prefKey == resources.getString(R.string.pref_key_user_theme)) {
                when (prefs.getString(prefKey, resources.getString(R.string.key_value_system_theme))) {
                    resources.getString(R.string.key_value_system_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    resources.getString(R.string.key_value_dark_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    resources.getString(R.string.key_value_light_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

    }
}