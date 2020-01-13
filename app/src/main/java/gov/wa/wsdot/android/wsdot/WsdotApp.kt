package gov.wa.wsdot.android.wsdot

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import gov.wa.wsdot.android.wsdot.di.AppInjector
import javax.inject.Inject

class WsdotApp : Application(), HasActivityInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        setDarkMode(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.pref_key_user_theme))
        super.onCreate()

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