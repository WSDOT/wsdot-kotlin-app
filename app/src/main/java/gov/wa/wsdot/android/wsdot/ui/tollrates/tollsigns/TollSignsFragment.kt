package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TollSignFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject

abstract class TollSignsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var tollSignsViewModel: TollSignsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<TollSignsListAdapter>()

    var binding by autoCleared<TollSignFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        tollSignsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(TollSignsViewModel::class.java)
        tollSignsViewModel.setRoute(getRoute(), "N")

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TollSignFragmentBinding>(
            inflater,
            R.layout.toll_sign_fragment,
            container,
            false
        )

        binding = dataBinding

        binding.viewModel = tollSignsViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        val adapter =
            TollSignsListAdapter(
                dataBindingComponent,
                appExecutors
            ){ sign ->
                tollSignsViewModel.updateFavorite(sign.id, !sign.favorite)
            }

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.tollSignList.addItemDecoration(itemDivider)

        binding.tollSignList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.tollSignList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        tollSignsViewModel.tollSigns.observe(viewLifecycleOwner, Observer { signResource ->
            signResource.data?.let { it ->

                adapter.submitList(it)
                if (it.isEmpty() && signResource.status != Status.LOADING) {
                    // binding.emptyListView.visibility = View.VISIBLE
                    binding.tollSignList.visibility = View.GONE
                } else {
                    // binding.emptyListView.visibility = View.GONE
                    binding.tollSignList.visibility = View.VISIBLE
                }
            }

        })

        return dataBinding.root
    }

    abstract fun getRoute(): Int

}