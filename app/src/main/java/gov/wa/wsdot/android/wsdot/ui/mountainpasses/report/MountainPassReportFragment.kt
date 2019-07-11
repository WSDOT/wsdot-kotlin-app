package gov.wa.wsdot.android.wsdot.ui.mountainpasses.report

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassReportFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListViewModel
import gov.wa.wsdot.android.wsdot.ui.common.SimpleFragmentPagerAdapter
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras.TerminalCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passCameras.PassCamerasListFragment
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject
import kotlin.collections.ArrayList

class MountainPassReportFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var passViewModel: MountainPassReportViewModel
    lateinit var cameraListViewModel: CameraListViewModel

    private var isFavorite: Boolean = false

    private lateinit var fragmentPagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager

    var binding by autoCleared<MountainPassReportFragmentBinding>()

    val args: MountainPassReportFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear view models since they are no longer needed
        viewModelStore.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // set up view models
        passViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MountainPassReportViewModel::class.java)
        passViewModel.setPassId(args.passId)

        cameraListViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(CameraListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<MountainPassReportFragmentBinding>(
            inflater,
            R.layout.mountain_pass_report_fragment,
            container,
            false
        )

        passViewModel.pass.observe(viewLifecycleOwner, Observer { pass ->
            if (pass.data != null) {
                isFavorite = pass.data.favorite
                activity?.invalidateOptionsMenu()

                val cameraIds = pass.data.cameras.map { it.id }
                cameraListViewModel.setCamerasQuery(cameraIds)

                // TODO: Only do this once
                viewPager = dataBinding.root.findViewById(R.id.pager)
                setupViewPager(viewPager, cameraIds.isNotEmpty(), pass.data.forecasts.isNotEmpty())
                val tabLayout: TabLayout = dataBinding.root.findViewById(R.id.tab_layout)
                tabLayout.setupWithViewPager(viewPager)


            }
        })

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
     //   viewPager = view.findViewById(R.id.pager)
     //   setupViewPager(viewPager)
      //  val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
     //   tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        setFavoriteMenuIcon(menu.findItem(R.id.action_favorite))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                passViewModel.updateFavorite(args.passId)
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
            menuItem.icon = resources.getDrawable(R.drawable.ic_menu_favorite_gray, null)
        }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager, withCameras: Boolean, withForecast: Boolean) {

        val fragments = ArrayList<Fragment>()
        fragments.add(FerryAlertsFragment())
        if (withCameras) { fragments.add(PassCamerasListFragment()) }
        if (withForecast) { fragments.add(FerryAlertsFragment()) }

        val titles = ArrayList<String>()
        titles.add("report")
        if (withCameras) { titles.add("cameras") }
        if (withForecast) { titles.add("forecast") }

        fragmentPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, fragments, titles)

        viewPager.adapter = fragmentPagerAdapter

    }

}