package gov.wa.wsdot.android.wsdot.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.HasSupportFragmentInjector
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.WsdotApp
import gov.wa.wsdot.android.wsdot.ui.eventbanner.EventBannerViewModel
import gov.wa.wsdot.android.wsdot.ui.notifications.NotificationsViewModel
import gov.wa.wsdot.android.wsdot.util.*
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

    private var adView: AdManagerAdView? = null

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            val adBannerBox: LinearLayout = findViewById(R.id.ad_banner_box)

            var adWidthPixels = adBannerBox.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
                R.id.navBridgeAlertsFragment,
                R.id.navBorderCrossingsFragment,
                R.id.navTollRatesFragment,
                R.id.navFavoritesFragment,
                R.id.navAmtrakCascadesFragment,
                R.id.navNotificationsFragment,
                R.id.navAboutFragment,
                R.id.navSettingsFragment
            ), drawerLayout
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.navTrafficMapFragment -> navView.menu.findItem(R.id.nav_traffic_map).isChecked =
                    true
                R.id.navFerriesHomeFragment -> navView.menu.findItem(R.id.nav_ferries).isChecked =
                    true
                R.id.navMountainPassHomeFragment -> navView.menu.findItem(R.id.nav_mountain_passes).isChecked =
                    true
                R.id.navBridgeAlertsFragment -> navView.menu.findItem(R.id.nav_bridge_alerts).isChecked =
                    true
                R.id.navTollRatesFragment -> navView.menu.findItem(R.id.nav_toll_rates).isChecked =
                    true
                R.id.navBorderCrossingsFragment -> navView.menu.findItem(R.id.nav_border_waits).isChecked =
                    true
                R.id.navAmtrakCascadesFragment -> navView.menu.findItem(R.id.nav_amtrak_cascades).isChecked =
                    true
                R.id.navFavoritesFragment -> navView.menu.findItem(R.id.nav_favorites).isChecked =
                    true
                R.id.navSettingsFragment -> navView.menu.findItem(R.id.nav_settings).isChecked =
                    true
                R.id.navAboutFragment -> navView.menu.findItem(R.id.nav_about).isChecked = true
                R.id.navNotificationsFragment -> navView.menu.findItem(R.id.nav_notifications).isChecked =
                    true
            }
        }

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)

        val startDestination = getStartDestination(intent?.extras)

        graph.setStartDestination(startDestination)

        navController.graph = graph

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
                    navView.menu.findItem(R.id.event_banner).actionView?.findViewById<TextView>(R.id.event_banner_text)?.text =
                        it.bannerText

                    editor.putInt(getString(R.string.pref_key_theme), it.themeId)
                    editor.putString(getString(R.string.pref_key_current_event), it.title)
                    editor.commit()

                    addMenuBadgeIfNeeded()

                } else {
                    navView.menu.setGroupVisible(R.id.event_banner_group, false)
                    navView.menu.findItem(R.id.event_banner).actionView?.findViewById<TextView>(R.id.event_banner_text)?.text = ""
                    editor.putString(getString(R.string.pref_key_last_seen_event), "")
                    editor.putString(getString(R.string.pref_key_current_event), "")
                    editor.putInt(getString(R.string.pref_key_theme), 0)
                    editor.apply()
                }
            }
        })

        notificationsViewModel = ViewModelProvider(this, viewModelFactory).get(
            NotificationsViewModel::class.java
        )

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

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) { }

        adView = AdManagerAdView(this)
//        adBannerBox.addView(adView)
        adView?.setAdSizes(adSize)
        adView?.adUnitId = ApiKeys.UNIT_ID

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        addMenuBadgeIfNeeded()
        
        // display menu event banner if available
        val navView: NavigationView = findViewById(R.id.drawer_nav_view)
        navView.setNavigationItemSelectedListener(this)

        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventBannerViewModel::class.java)
        eventViewModel.eventStatus.observe(this, Observer { eventResponse ->
            eventResponse.data?.let {

                val settings = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = settings.edit()

                if (TimeUtils.currentDateInRange(it.startDate, it.endDate, "yyyy-MM-dd")) {

                    eventTitle = it.title
                    navView.menu.setGroupVisible(R.id.event_banner_group, true)
                    navView.menu.findItem(R.id.event_banner).actionView?.findViewById<TextView>(R.id.event_banner_text)?.text =
                        it.bannerText

                    editor.putInt(getString(R.string.pref_key_theme), it.themeId)
                    editor.putString(getString(R.string.pref_key_current_event), it.title)
                    editor.commit()

                    addMenuBadgeIfNeeded()

                } else {
                    navView.menu.setGroupVisible(R.id.event_banner_group, false)
                    navView.menu.findItem(R.id.event_banner).actionView?.findViewById<TextView>(R.id.event_banner_text)?.text = ""
                    editor.putString(getString(R.string.pref_key_last_seen_event), "")
                    editor.putString(getString(R.string.pref_key_current_event), "")
                    editor.putInt(getString(R.string.pref_key_theme), 0)
                    editor.apply()
                }
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun addMenuBadgeIfNeeded() {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)

        val lastSeenEvent = settings.getString(getString(R.string.pref_key_last_seen_event), "")
        val currentEvent = settings.getString(getString(R.string.pref_key_current_event), "")

        if (lastSeenEvent != currentEvent) {
            val actionbar = supportActionBar
            actionbar?.setHomeAsUpIndicator(
                BadgeDrawable.getMenuBadge(
                    this,
                    R.drawable.ic_menu,
                    "!"
                )
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.extras?.let { handleExtras(it) }
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
            getString(R.string.key_value_bridge_alerts_nav) -> {
                navView.menu.findItem(R.id.nav_bridge_alerts).isChecked = true
                return R.id.navBridgeAlertsFragment
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

            // Handles when app receives a TRAFFIC_ALERT type
            extras.getBoolean(getString(R.string.push_alert_traffic_alert), false) -> {

                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_traffic_map).isChecked = true

                val settings = PreferenceManager.getDefaultSharedPreferences(this)

                val latitude = settings.getDouble(
                    getString(R.string.user_preference_traffic_map_latitude),
                    47.6062
                )
                val longitude = settings.getDouble(
                    getString(R.string.user_preference_traffic_map_longitude),
                    -122.3321
                )

                val lat = extras.getDouble(
                    getString(R.string.push_alert_traffic_alert_latitude),
                    latitude
                )
                val lng = extras.getDouble(
                    getString(R.string.push_alert_traffic_alert_longitude),
                    longitude
                )

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
                val title = extras.getString(getString(R.string.push_alert_traffic_alert_title), "")

                // reset navigation to the traffic map
                findNavController(R.id.nav_host_fragment).navigate(R.id.navTrafficMapFragment)
                findNavController(R.id.nav_host_fragment).popBackStack(
                    R.id.navTrafficMapFragment,
                    false
                )

                val action = NavGraphDirections.actionGlobalNavHighwayAlertFragment(
                    alertId,
                    title
                )
                findNavController(R.id.nav_host_fragment).navigate(action)

            }

            // Handles when app receives a FERRY_ALERT type
            extras.getBoolean(getString(R.string.push_alert_ferry_alert), false) -> {

                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_ferries).isChecked = true

                val alertId = extras.getInt(getString(R.string.push_alert_ferry_alert_id), 0)
                val routeId = extras.getInt(getString(R.string.push_alert_ferry_route_id), 0)
                val routeTitle = extras.getString(
                    getString(R.string.push_alert_ferry_route_title),
                    ""
                )

                findNavController(R.id.nav_host_fragment).navigate(R.id.navFerriesHomeFragment)
                findNavController(R.id.nav_host_fragment).popBackStack(
                    R.id.navFerriesHomeFragment,
                    false
                )

                val actionOne = NavGraphDirections.actionGlobalNavFerriesRouteFragment(
                    routeId,
                    routeTitle
                )
                findNavController(R.id.nav_host_fragment).navigate(actionOne)

                val actionTwo = NavGraphDirections.actionGlobalNavFerryAlertDetailsFragment(alertId, routeTitle)
                findNavController(R.id.nav_host_fragment).navigate(actionTwo)

            }

            // Handles when app receives a BRIDGE_ALERT type
            extras.getBoolean(getString(R.string.push_alert_bridge_alert), false) -> {

                val navView: NavigationView = findViewById(R.id.drawer_nav_view)
                navView.menu.findItem(R.id.nav_traffic_map).isChecked = true

                val settings = PreferenceManager.getDefaultSharedPreferences(this)

                val latitude = settings.getDouble(
                    getString(R.string.user_preference_traffic_map_latitude),
                    47.6062
                )

                val longitude = settings.getDouble(
                    getString(R.string.user_preference_traffic_map_longitude),
                    -122.3321
                )

                val lat = extras.getDouble(
                    getString(R.string.push_alert_bridge_alert_latitude),
                    latitude
                )
                val lng = extras.getDouble(
                    getString(R.string.push_alert_bridge_alert_longitude),
                    longitude
                )

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

                val alertId = extras.getInt(getString(R.string.push_alert_bridge_alert_id), 0)
                val alertTitle = extras.getString(getString(R.string.push_alert_bridge_alert_title), "Bridge Alert")

                // reset navigation to the traffic map
                findNavController(R.id.nav_host_fragment).navigate(R.id.navTrafficMapFragment)
                findNavController(R.id.nav_host_fragment).popBackStack(
                    R.id.navTrafficMapFragment,
                    false
                )
                findNavController(R.id.nav_host_fragment).navigate(R.id.navBridgeAlertsFragment)
                findNavController(R.id.nav_host_fragment).popBackStack(
                    R.id.navBridgeAlertsFragment,
                    false
                )

                val action = NavGraphDirections.actionGlobalNavBridgeAlertFragment(
                    alertId,
                    alertTitle
                )
                findNavController(R.id.nav_host_fragment).navigate(action)

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

        if (themeId == 0) {
            theme.applyStyle(R.style.ThemeWSDOTGreen, true)
        }
        else if (themeId == 1) {
            theme.applyStyle(R.style.ThemeWSDOTOrange, true)
        }
        else if (themeId == 2) {
            theme.applyStyle(R.style.ThemeWSDOTBlue, true)
        }
        else if (themeId == 3) {
            theme.applyStyle(R.style.ThemeWSDOTCustom, true)
        }
        else if (themeId == 4) {
            theme.applyStyle(R.style.ThemeWSDOTEmergency, true)
        }

        else {
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

        eventViewModel.refresh()
        invalidateOptionsMenu()

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
            R.id.nav_bridge_alerts -> {
                navLocation = getString(R.string.key_value_bridge_alerts_nav)
                if (navController.currentDestination?.id != R.id.navBridgeAlertsFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navBridgeAlertsFragment)
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

//        adBannerBox?.visibility = VISIBLE

        //val testDeviceIds = Arrays.asList("2531EB5FD75758B5E8BDD4669A870BF7")
        //val configuration = RequestConfiguration.Builder()
            //.setTestDeviceIds(testDeviceIds)
            //.setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            //.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            //.build()
        //MobileAds.setRequestConfiguration(configuration)

        adView?.pause()
//        adView?.adListener = null
        adView?.adListener = object : AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()

                // report ad ID to crashlytics
                val info = adView?.responseInfo
                var adResponseId = "null"
                if (info != null){
                    Log.e("Ads", info.toString())
                }
                Log.d("Ads", "onAdLoaded: Ad response ID is $adResponseId")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("Ads", error.toString())
            }
        }

        val adRequest = AdManagerAdRequest.Builder()

        for ((key, value) in targets) {
            adRequest.addCustomTargeting(key, value)
        }

        // Start loading the ad in the background.
        adView?.loadAd(adRequest.build())

    }

    /**
     * Remove the ad so it doesn't take up any space.
     *  WARNING: don't call in onCreate
     */
    fun disableAds() {

//        adBannerBox?.visibility = GONE
        adView?.pause()
    }

    fun setScreenName(screenName: String) {
        firebaseAnalytics.setCurrentScreen(this, screenName, null)
    }

    fun updateTopicSub(topic: String, isSubbed: Boolean) {
        if (isSubbed) {
            // undo sub if network request fails
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
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
                    if (!task.isSuccessful) {
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

}