package gov.wa.wsdot.android.wsdot.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.HasSupportFragmentInjector
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.WsdotApp
import gov.wa.wsdot.android.wsdot.ui.notifications.NotificationsViewModel
import gov.wa.wsdot.android.wsdot.util.BadgeDrawable
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.putDouble
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HasSupportFragmentInjector, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var eventViewModel: EventBannerViewModel
    lateinit var notificationsViewModel: NotificationsViewModel

    lateinit var drawerLayout: DrawerLayout

    private var eventTitle = "WSDOT"

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.drawer_nav_view)
        navView.setNavigationItemSelectedListener(this)

        // Navigation component setup with drawer
        val config = AppBarConfiguration(
            setOf(
                R.id.navTrafficMapFragment,
                R.id.navFerriesHomeFragment,
                R.id.navMountainPassHomeFragment,
                R.id.navBorderCrossingsFragment,
                R.id.navTollRatesFragment,
                R.id.navFavoritesFragment,
                R.id.navAmtrakCascadesFragment
            ), drawerLayout)

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)

        val startDestination = getStartDestination(intent?.extras)

        graph.startDestination = startDestination

        navController.graph = graph

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.navTrafficMapFragment -> navView.menu.findItem(R.id.nav_traffic_map).isChecked = true
                R.id.navFerriesHomeFragment -> navView.menu.findItem(R.id.nav_ferries).isChecked = true
                R.id.navMountainPassHomeFragment -> navView.menu.findItem(R.id.nav_mountain_passes).isChecked = true
                R.id.navTollRatesFragment -> navView.menu.findItem(R.id.nav_toll_rates).isChecked = true
                R.id.navBorderCrossingsFragment -> navView.menu.findItem(R.id.nav_border_waits).isChecked = true
                R.id.navAmtrakCascadesFragment -> navView.menu.findItem(R.id.nav_amtrak_cascades).isChecked = true
                R.id.navFavoritesFragment -> navView.menu.findItem(R.id.nav_favorites).isChecked = true
                R.id.navSettingsFragment -> navView.menu.findItem(R.id.nav_settings).isChecked = true
                R.id.navAboutFragment -> navView.menu.findItem(R.id.nav_about).isChecked = true
                R.id.navNotificationsFragment -> navView.menu.findItem(R.id.nav_notifications).isChecked = true
            }
        }

        NavigationUI.setupWithNavController(findViewById(R.id.toolbar), navController, config)
        NavigationUI.setupActionBarWithNavController(this, navController, config)

        intent?.extras?.let {
            handleExtras(it)
        }

        // handle event banner
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventBannerViewModel::class.java)
        eventViewModel.eventStatus.observe(this, Observer { eventResponse ->
            eventResponse.data?.let {

                val settings = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = settings.edit()

                if (TimeUtils.currentDateInRange(it.startDate, it.endDate, "yyyy-MM-dd")) {

                    eventTitle = it.title
                    navView.menu.setGroupVisible(R.id.event_banner_group, true)
                    navView.menu.findItem(R.id.event_banner).actionView.findViewById<TextView>(R.id.event_banner_text).text = it.bannerText

                    editor.putInt(getString(R.string.pref_key_theme), it.themeId)
                    editor.putString(getString(R.string.pref_key_current_event), it.title)
                    editor.commit()

                    addMenuBadgeIfNeeded()

                } else {
                    navView.menu.setGroupVisible(R.id.event_banner_group, false)

                    editor.putString(getString(R.string.pref_key_last_seen_event), "")
                    editor.putString(getString(R.string.pref_key_current_event), "")
                    editor.putInt(getString(R.string.pref_key_theme), 0)
                    editor.apply()
                }
            }
        })

        notificationsViewModel = ViewModelProvider(this, viewModelFactory).get(NotificationsViewModel::class.java)

        notificationsViewModel.topics.observe(this, Observer { topics ->
            topics.data?.let {
                for (topic in it) {
                    notificationsViewModel.updateSubscription(
                        topic.topic,
                        topic.subscribed
                    )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        showMenuPrompt()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        addMenuBadgeIfNeeded()
        return super.onCreateOptionsMenu(menu)
    }

    private fun addMenuBadgeIfNeeded() {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)

        val lastSeenEvent = settings.getString(getString(R.string.pref_key_last_seen_event), "")
        val currentEvent = settings.getString(getString(R.string.pref_key_current_event), "")

        if (lastSeenEvent != currentEvent) {
            val actionbar = supportActionBar
            actionbar?.setHomeAsUpIndicator(BadgeDrawable.getMenuBadge(this, R.drawable.ic_menu, "!"))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.let { handleExtras(it) }
    }

    private fun getStartDestination(extras: Bundle?): Int {

        val navView: NavigationView = findViewById(R.id.drawer_nav_view)

        return if (extras != null) {
            when {
                extras.getString("shortcut_id") == "ferries" -> {
                    navView.menu.findItem(R.id.nav_ferries).isChecked = true
                    R.id.navFerriesHomeFragment
                }
                extras.getString("shortcut_id") == "mountain_passes" -> {
                    navView.menu.findItem(R.id.nav_mountain_passes).isChecked = true
                    R.id.navMountainPassHomeFragment
                }
                extras.getString("shortcut_id") == "favorites" -> {
                    navView.menu.findItem(R.id.nav_favorites).isChecked = true
                    R.id.navFavoritesFragment
                }
                else -> {
                    navToLastLocation(navView)
                }
            }
        } else {
            navToLastLocation(navView)
        }
    }

    private fun navToLastLocation(navView: NavigationView): Int {

        val settings = PreferenceManager.getDefaultSharedPreferences(this)

        when (settings.getString(getString(R.string.pref_key_last_nav_location), "")) {
            getString(R.string.key_value_ferries_nav) -> {
                navView.menu.findItem(R.id.nav_ferries).isChecked = true
                return R.id.navFerriesHomeFragment
            }
            getString(R.string.key_value_mountain_passes_nav) -> {
                navView.menu.findItem(R.id.nav_mountain_passes).isChecked = true
                return R.id.navMountainPassHomeFragment
            }
            getString(R.string.key_value_toll_rates_nav) -> {
                navView.menu.findItem(R.id.nav_toll_rates).isChecked = true
                return R.id.navTollRatesFragment
            }
            getString(R.string.key_value_border_waits_nav) -> {
                navView.menu.findItem(R.id.nav_border_waits).isChecked = true
                return R.id.navBorderCrossingsFragment
            }
            getString(R.string.key_value_amtrak_nav) -> {
                navView.menu.findItem(R.id.nav_amtrak_cascades).isChecked = true
                return R.id.navAmtrakCascadesFragment
            }
            getString(R.string.key_value_favorites_nav) -> {
                navView.menu.findItem(R.id.nav_favorites).isChecked = true
                return R.id.navFavoritesFragment
            }
        }

        navView.menu.findItem(R.id.nav_traffic_map).isChecked = true
        return R.id.navTrafficMapFragment

    }

    private fun handleExtras(extras: Bundle) {
        when {
            extras.getBoolean(getString(R.string.push_alert_traffic_alert), false) -> {

                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_traffic_map).isChecked = true

                val settings = PreferenceManager.getDefaultSharedPreferences(this)

                val latitude = settings.getDouble(getString(R.string.user_preference_traffic_map_latitude), 47.6062)
                val longitude = settings.getDouble(getString(R.string.user_preference_traffic_map_longitude), -122.3321)

                val lat = extras.getDouble(getString(R.string.push_alert_traffic_alert_latitude), latitude)
                val lng = extras.getDouble(getString(R.string.push_alert_traffic_alert_longitude), longitude)

                val editor = settings.edit()
                editor.putDouble(
                    getString(R.string.user_preference_traffic_map_latitude),
                    lat
                )
                editor.putDouble(
                    getString(R.string.user_preference_traffic_map_longitude),
                    lng
                )
                editor.putFloat(
                    getString(R.string.user_preference_traffic_map_zoom),
                    12.0f
                )
                editor.apply()

                val alertId = extras.getInt(getString(R.string.push_alert_traffic_alert_id), 0)

                // reset navigation to the traffic map
                findNavController(R.id.nav_host_fragment).navigate(R.id.navTrafficMapFragment)
                findNavController(R.id.nav_host_fragment).popBackStack(R.id.navTrafficMapFragment, false)

                val action = NavGraphDirections.actionGlobalNavHighwayAlertFragment(alertId, "Traffic Alert")
                findNavController(R.id.nav_host_fragment).navigate(action)

            }

            extras.getBoolean(getString(R.string.push_alert_ferry_alert), false) -> {

                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_ferries).isChecked = true

                val alertId = extras.getInt(getString(R.string.push_alert_ferry_alert_id), 0)
                val routeId = extras.getInt(getString(R.string.push_alert_ferry_route_id), 0)
                val routeTitle = extras.getString(getString(R.string.push_alert_ferry_route_title), "")

                findNavController(R.id.nav_host_fragment).navigate(R.id.navFerriesHomeFragment)
                findNavController(R.id.nav_host_fragment).popBackStack(R.id.navFerriesHomeFragment, false)

                val actionOne = NavGraphDirections.actionGlobalNavFerriesRouteFragment(routeId, routeTitle)
                findNavController(R.id.nav_host_fragment).navigate(actionOne)

                val actionTwo = NavGraphDirections.actionGlobalNavFerryAlertDetailsFragment(alertId)
                findNavController(R.id.nav_host_fragment).navigate(actionTwo)

            }
        }

        when {
            extras.getString("shortcut_id") == "ferries" -> {
                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_ferries).isChecked = true
            }

            extras.getString("shortcut_id") == "mountain_passes" -> {
                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_mountain_passes).isChecked = true
            }

            extras.getString("shortcut_id") == "favorites" -> {
                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_favorites).isChecked = true
            }
        }

        // reset intent so we don't reuse it on config changes.
        intent.replaceExtras(Bundle())
        intent.action = ""
        intent.data = null
        intent.flags = 0

    }
    
    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()

        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val themeId = settings.getInt(getString(R.string.pref_key_theme), 0)

        if (themeId == 1) {
            theme.applyStyle(R.style.ThemeWSDOTOrange, true)
        } else {
            theme.applyStyle(R.style.ThemeWSDOTGreen, true)
        }
        return theme
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = settings.edit()
        var navLocation = settings.getString(getString(R.string.pref_key_last_nav_location), "")

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        when (item.itemId) {
            R.id.nav_traffic_map -> {
                navLocation = getString(R.string.key_value_traffic_nav)
                if (navController.currentDestination?.id != R.id.navTrafficMapFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navTrafficMapFragment)
                }
            }
            R.id.nav_ferries -> {
                navLocation = getString(R.string.key_value_ferries_nav)
                if (navController.currentDestination?.id != R.id.navFerriesHomeFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navFerriesHomeFragment)
                }
            }
            R.id.nav_mountain_passes -> {
                navLocation = getString(R.string.key_value_mountain_passes_nav)
                if (navController.currentDestination?.id != R.id.navMountainPassHomeFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navMountainPassHomeFragment)
                }
            }
            R.id.nav_toll_rates -> {
                navLocation = getString(R.string.key_value_toll_rates_nav)
                if (navController.currentDestination?.id != R.id.navTollRatesFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navTollRatesFragment)
                }
            }
            R.id.nav_border_waits -> {
                navLocation = getString(R.string.key_value_border_waits_nav)
                if (navController.currentDestination?.id != R.id.navBorderCrossingsFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navBorderCrossingsFragment)
                }
            }
            R.id.nav_amtrak_cascades -> {
                navLocation = getString(R.string.key_value_amtrak_nav)
                if (navController.currentDestination?.id != R.id.navAmtrakCascadesFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navAmtrakCascadesFragment)
                }
            }
            R.id.nav_favorites -> {
                navLocation = getString(R.string.key_value_favorites_nav)
                if (navController.currentDestination?.id != R.id.navFavoritesFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navFavoritesFragment)
                }
            }
            R.id.nav_settings -> {
                if (navController.currentDestination?.id != R.id.navSettingsFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navSettingsFragment)
                }
            }
            R.id.nav_about -> {
                if (navController.currentDestination?.id != R.id.navAboutFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navAboutFragment)
                }
            }
            R.id.event_banner -> {
                if (navController.currentDestination?.id != R.id.navEventDetailsFragment) {
                    val action = NavGraphDirections.actionGlobalNavEventDetailsFragment(eventTitle)
                    findNavController(R.id.nav_host_fragment).navigate(action)
                }
            }
            R.id.nav_notifications -> {
                if (navController.currentDestination?.id != R.id.navNotificationsFragment) {
                    val action = NavGraphDirections.actionGlobalNavNotificationsFragment()
                    findNavController(R.id.nav_host_fragment).navigate(action)
                }
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)

        editor.putString(getString(R.string.pref_key_last_nav_location), navLocation)
        editor.apply()

        return true
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    /**
     * Initialize and display ads.
     * WARNING: don't call in onCreate
     */
     fun enableAds(targets: Map<String, String>) {

        val mAdViewBox: LinearLayout = drawerLayout.findViewById(R.id.ad_banner_box)
        mAdViewBox.visibility = VISIBLE

        val mAdView: PublisherAdView = drawerLayout.findViewById(R.id.publisherAdView)
        mAdView.visibility = VISIBLE

        val adRequest = PublisherAdRequest.Builder()

        for ((key, value) in targets) {
            println("$key = $value")
            adRequest.addCustomTargeting(key, value)
        }

        mAdView.adListener = null
        mAdView.adListener = object : AdListener() {

            override fun onAdLoaded() {
                mAdView.visibility = VISIBLE
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("debug", "failed to load ad")
                Log.e("debug", error.domain)
                Log.e("debug", error.code.toString())
                Log.e("debug", error.message)
                Log.e("debug", error.cause.toString())
            }
        }

        mAdView.loadAd(adRequest.build())

    }

    /**
     * Remove the ad so it doesn't take up any space.
     *  WARNING: don't call in onCreate
     */
    fun disableAds() {
        val mAdView: PublisherAdView = drawerLayout.findViewById(R.id.publisherAdView)
        val mAdViewBox: LinearLayout = drawerLayout.findViewById(R.id.ad_banner_box)
        mAdViewBox.visibility = GONE
        mAdView.visibility = GONE
        mAdView.pause()
    }

    fun setScreenName(screenName: String) {
        firebaseAnalytics.setCurrentScreen(this, screenName, null)
    }

    fun updateTopicSub(topic: String, isSubbed: Boolean) {
        if (isSubbed) {
            // undo sub if network request fails
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                        notificationsViewModel.updateSubscription(
                            topic,
                            !isSubbed
                        )
                    }
                }
        } else {
            // undo sub if network request fails
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->

                    var msg = getString(R.string.msg_unsubscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_unsubscribe_failed)
                        notificationsViewModel.updateSubscription(
                            topic,
                            !isSubbed
                        )
                    }
                }
        }
    }

    // Pref change listener
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, prefKey: String?) {
        (application as WsdotApp).setDarkMode(sharedPreferences, prefKey)
    }

    // tap target for new users
    private fun showMenuPrompt() {
        try {
            val prefManager = PreferenceManager.getDefaultSharedPreferences(this)
            if (!prefManager.getBoolean("didShowPrompt", false)) {
                TapTargetView.showFor(this,
                    TapTarget.forView(
                        getNavButtonView(),
                        "Know Before You Go!",
                        "Tap here for Ferry Schedules, Mountain Pass Reports and more."
                    ),
                    object : TapTargetView.Listener() {
                        override fun onTargetDismissed(
                            view: TapTargetView?,
                            userInitiated: Boolean
                        ) {
                            val prefEditor = prefManager.edit()
                            prefEditor.putBoolean("didShowPrompt", true)
                            prefEditor.apply()
                            super.onTargetDismissed(view, userInitiated)
                        }

                    }
                )
            }
        } catch (e: NullPointerException) {
            return
        }
    }

    private fun getNavButtonView(): View? {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        for (x in 0..toolbar.childCount) {
            if (toolbar.getChildAt(x).id != R.id.action_my_location && toolbar.getChildAt(x) is ImageButton) {
                return toolbar.getChildAt(x)
            }
        }
        return null
    }
}