package gov.wa.wsdot.android.wsdot.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.google.android.material.navigation.NavigationView
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.HasSupportFragmentInjector
import gov.wa.wsdot.android.wsdot.R
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

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
                R.id.navFavoritesFragment
            ), drawerLayout)

        NavigationUI.setupWithNavController(findViewById(R.id.toolbar), navController, config)
        NavigationUI.setupActionBarWithNavController(this, navController, config)

        // TODO: Let user set home screen ///////////////////////
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)
        graph.startDestination = R.id.navTollRatesFragment
        navController.graph = graph
        navView.menu.getItem(0).isChecked = true
     //   enableAds(resources.getString(R.string.ad_target_traffic))
        /////////////////////////////////////////////////////////



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

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        when (item.itemId) {
            R.id.nav_traffic_map -> {
                if (navController.currentDestination?.id != R.id.navTrafficMapFragment) {
                    enableAds(resources.getString(R.string.ad_target_traffic))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navTrafficMapFragment)
                }
            }
            R.id.nav_ferries -> {
                if (navController.currentDestination?.id != R.id.navFerriesHomeFragment) {
                    enableAds(resources.getString(R.string.ad_target_ferries))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navFerriesHomeFragment)
                }
            }
            R.id.nav_mountain_passes -> {
                if (navController.currentDestination?.id != R.id.navMountainPassHomeFragment) {
                    enableAds(resources.getString(R.string.ad_target_passes))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navMountainPassHomeFragment)
                }
            }
            R.id.nav_toll_rates -> {
                if (navController.currentDestination?.id != R.id.navTollRatesFragment) {
                    disableAds()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navTollRatesFragment)
                }
            }
            R.id.nav_border_waits -> {
                if (navController.currentDestination?.id != R.id.navBorderCrossingsFragment) {
                    enableAds("other")
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navBorderCrossingsFragment)
                }
            }
            /*
            R.id.nav_amtrak_cascades -> {
                enableAds(drawerLayout, resources.getString(R.string.ad_target_other))
            }
             */
            R.id.nav_favorites -> {
                // Favorites fragment handles its out ad targets
                if (navController.currentDestination?.id != R.id.navFavoritesFragment) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_navFavoritesFragment)
                }
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    /**
     * Initialize and display ads.
     */
     fun enableAds(target: String) {
        val mAdViewBox: LinearLayout = drawerLayout.findViewById(R.id.ad_banner_box)
        mAdViewBox.visibility = VISIBLE

        val mAdView: PublisherAdView = drawerLayout.findViewById(R.id.publisherAdView)

        val adRequest = PublisherAdRequest.Builder()
            .addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR) // All emulators
            .addCustomTargeting("wsdotapp", target)
            .build()

       // mAdView.visibility = View.GONE
        mAdView.adListener = null

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(error: Int) {
                super.onAdFailedToLoad(error)
                when (error) {
                    AdRequest.ERROR_CODE_NO_FILL -> Log.e("debug", "no fill")
                    AdRequest.ERROR_CODE_INVALID_REQUEST -> Log.e("debug", "invalid request")
                    AdRequest.ERROR_CODE_NETWORK_ERROR -> Log.e("debug", "network error")
                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> Log.e("debug", "internal error")
                }
            }
        }
        mAdView.loadAd(adRequest)
    }

    /**
     * Remove the ad so it doesn't take up any space.
     */
    fun disableAds() {
        val mAdView: PublisherAdView = drawerLayout.findViewById(R.id.publisherAdView)
        val mAdViewBox: LinearLayout = drawerLayout.findViewById(R.id.ad_banner_box)
        mAdViewBox.visibility = GONE
        mAdView.visibility = GONE
        mAdView.pause()
    }

}
