package gov.wa.wsdot.android.wsdot.ui.mountainpasses.report

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassReportFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListViewModel
import gov.wa.wsdot.android.wsdot.ui.common.SimpleFragmentPagerAdapter
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passCameras.PassCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passConditions.PassConditionsFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passForecast.PassForecastListFragment
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class MountainPassReportFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var passReportViewModel: MountainPassReportViewModel
    lateinit var cameraListViewModel: CameraListViewModel

    private var isFavorite: Boolean = false

    private lateinit var fragmentPagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager

    var binding by autoCleared<MountainPassReportFragmentBinding>()

    val args: MountainPassReportFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Clear view models since they are no longer needed
        viewModelStore.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set up view models
        passReportViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(MountainPassReportViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        passReportViewModel.setPassId(args.passId)

        cameraListViewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(CameraListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<MountainPassReportFragmentBinding>(
            inflater,
            R.layout.mountain_pass_report_fragment,
            container,
            false
        )

        passReportViewModel.pass.observe(viewLifecycleOwner, Observer { pass ->
            if (pass.data != null) {
                isFavorite = pass.data.favorite
                activity?.invalidateOptionsMenu()

                val cameraIds = pass.data.cameras.map { it.id }
                cameraListViewModel.setCamerasQuery(cameraIds)

                viewPager = dataBinding.root.findViewById(R.id.pager)

                // only add the adapter if the view pager doesn't have one
                // This prevents tabs from resetting when forecasts and favorite is updated
                if (viewPager.adapter == null) {
                    setupViewPager(viewPager, cameraIds.isNotEmpty(), pass.data.forecasts.isNotEmpty())
                }

                val tabLayout: TabLayout = dataBinding.root.findViewById(R.id.tab_layout)
                tabLayout.setupWithViewPager(viewPager)

            }
        })

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        return dataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pass_menu, menu)
        setFavoriteMenuIcon(menu.findItem(R.id.action_favorite))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                passReportViewModel.updateFavorite(args.passId)
                return false
            }
            R.id.action_refresh -> {
                passReportViewModel.refresh()
                Toast.makeText(context, "refreshing report...", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {}
        }
        return false
    }

    private fun setFavoriteMenuIcon(menuItem: MenuItem){
        if (isFavorite) {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_pink, null)
        } else {
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_outline, null)
        }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager, withCameras: Boolean, withForecast: Boolean) {

        val fragments = ArrayList<Fragment>()
        fragments.add(PassConditionsFragment())
        if (withCameras) { fragments.add(PassCamerasListFragment()) }
        if (withForecast) { fragments.add(PassForecastListFragment()) }

        val titles = ArrayList<String>()
        titles.add("report")
        if (withCameras) { titles.add("cameras") }
        if (withForecast) { titles.add("forecast") }

        fragmentPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, fragments, titles)

        viewPager.adapter = fragmentPagerAdapter

    }

}