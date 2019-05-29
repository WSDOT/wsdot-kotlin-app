package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerriesRouteFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import gov.wa.wsdot.android.wsdot.ui.common.callback.TapCallback
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import java.util.Calendar.*
import javax.inject.Inject
import androidx.viewpager.widget.ViewPager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import gov.wa.wsdot.android.wsdot.ui.common.SimpleFragmentPagerAdapter
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingViewModel


class FerriesRouteFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var routeViewModel: FerriesRouteViewModel
    lateinit var sailingViewModel: FerriesSailingViewModel

    lateinit var dayPickerViewModel: SharedDateViewModel

    private lateinit var fragmentPagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager

    //var fragmentDataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FerriesRouteFragmentBinding>()

    val args: FerriesRouteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sailingViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesSailingViewModel::class.java)

        routeViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesRouteViewModel::class.java)
        routeViewModel.setRouteId(args.routeId)

        dayPickerViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val dataBinding = DataBindingUtil.inflate<FerriesRouteFragmentBinding>(
            inflater,
            R.layout.ferries_route_fragment,
            container,
            false
        )

        dataBinding.datePickerCallback = object : TapCallback {
            override fun onTap(view: View) {
                val action = FerriesRouteFragmentDirections.actionNavFerriesRouteFragmentToDayPickerDialogFragment(args.title)
                view.findNavController().navigate(action)
            }
        }


        dayPickerViewModel.value.observe(viewLifecycleOwner, Observer { date ->
            val c = getInstance()
            if (date != null) {
                c.time = date
            }
            c.set(HOUR_OF_DAY, 0)
            c.set(MINUTE, 0)
            c.set(SECOND, 0)
            c.set(MILLISECOND, 0)
            Log.e("debug", c.time.toString())
            sailingViewModel.setSailingQuery(c.time)
        })

        //// Move to sailings fragment //////////////

        val c = getInstance()
        c.set(HOUR_OF_DAY, 0)
        c.set(MINUTE, 0)
        c.set(SECOND, 0)
        c.set(MILLISECOND, 0)
        Log.e("debug", c.time.toString())
        sailingViewModel.setSailingQuery(6, 8, 12, c.time)

        sailingViewModel.sailings.observe(viewLifecycleOwner, Observer { sailingData ->

            Log.e("debug", "got some sailing data...")
            Log.e("debug", sailingData.data.toString())

        })

        ////////////////////////////////////


        routeViewModel.selectedTerminalCombo.observe(viewLifecycleOwner, Observer { terminal ->
            Log.e("debug", terminal.toString())
        })

        dataBinding.dateViewModel = dayPickerViewModel
        dataBinding.routeViewModel = routeViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        binding = dataBinding

        return dataBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val routeId = args.routeId
        Log.e("debug", routeId.toString())
        Log.e("debug", "on create view")

        viewPager = view.findViewById(R.id.pager)
        setupViewPager(viewPager)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ferry_route_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager) {

        val fragments = ArrayList<Fragment>()
        fragments.add(FerriesSailingFragment())
        fragments.add(FerriesSailingFragment())
        fragments.add(FerriesSailingFragment())

        val titles = ArrayList<String>()
        titles.add("sailings")
        titles.add("cameras")
        titles.add("alerts")

        fragmentPagerAdapter = SimpleFragmentPagerAdapter(requireFragmentManager(), fragments, titles)

        viewPager.adapter = fragmentPagerAdapter


    }

}