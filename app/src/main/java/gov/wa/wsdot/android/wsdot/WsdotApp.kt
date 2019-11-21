package gov.wa.wsdot.android.wsdot

//import timber.log.Timber
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

        setDarkMode(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.key_darkmodesystem))
        super.onCreate()
        AppInjector.init(this)

    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    fun setDarkMode(sharedPreferences: SharedPreferences?, prefKey: String?) {

        sharedPreferences?.let { prefs ->
            if (prefKey == resources.getString(R.string.key_darkmode)) {
                val darkmode: Boolean = prefs.getBoolean(prefKey, false)

                if (darkmode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

            } else if (prefKey == resources.getString(R.string.key_darkmodesystem)) {
                val useSystem = prefs.getBoolean(prefKey, false)
                if (useSystem) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    if (prefs.getBoolean(resources.getString(R.string.key_darkmode), false)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }


}