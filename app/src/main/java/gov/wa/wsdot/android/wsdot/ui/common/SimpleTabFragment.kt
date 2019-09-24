package gov.wa.wsdot.android.wsdot.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TabLayoutBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

abstract class SimpleTabFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var fragmentPagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager

    var binding by autoCleared<TabLayoutBinding>()


    override fun onDestroy() {
        super.onDestroy()
        //Clear view models since they are no longer needed
        viewModelStore.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TabLayoutBinding>(
            inflater,
            R.layout.tab_layout,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        binding = dataBinding

        viewPager = dataBinding.root.findViewById(R.id.pager)
        setupViewPager(viewPager)
        val tabLayout: TabLayout = dataBinding.root.findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        return dataBinding.root
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager) {
        fragmentPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, getFragments(), getTitles())
        viewPager.adapter = fragmentPagerAdapter
    }

    abstract fun getFragments(): ArrayList<Fragment>
    abstract fun getTitles(): ArrayList<String>


}