package gov.wa.wsdot.android.wsdot.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import gov.wa.wsdot.android.wsdot.R
import android.content.SharedPreferences
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val favSortPref = findPreference<Preference>(resources.getString(R.string.pref_key_favorites_sort_order))
        favSortPref?.let {
            it.setOnPreferenceClickListener {
                navigateToFavSortSetting()
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).disableAds()
        // Set up a listener whenever a key changes
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(activity as SharedPreferences.OnSharedPreferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener whenever a key changes
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(activity as SharedPreferences.OnSharedPreferenceChangeListener)
    }

    private fun navigateToFavSortSetting(){
        val action = NavGraphDirections.actionGlobalNavFavoritesSortSettingFragment()
        findNavController().navigate(action)
    }


}