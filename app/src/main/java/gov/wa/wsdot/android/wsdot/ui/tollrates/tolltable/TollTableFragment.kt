package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TollRateTableFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.model.common.Status
import javax.inject.Inject

abstract class TollTableFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var tollRateTableViewModel: TollRateTableViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    private lateinit var spinner: Spinner

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<TollTableListAdapter>()

    var binding by autoCleared<TollRateTableFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        tollRateTableViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TollRateTableViewModel::class.java)
        tollRateTableViewModel.setRoute(getRoute())

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TollRateTableFragmentBinding>(
            inflater,
            R.layout.toll_rate_table_fragment,
            container,
            false
        )

        binding = dataBinding

        val content = SpannableString(getInfoLinkText())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.infoButton.text = content

        binding.infoButton.setTextColor(resources.getColor(R.color.wsdotGreen))

        binding.infoButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(getInfoLinkURL())
            startActivity(intent)
        }

        binding.viewModel = tollRateTableViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter =
            TollTableListAdapter(
                dataBindingComponent,
                appExecutors
            )

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.tollTableList.addItemDecoration(itemDivider)

        binding.tollTableList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.tollTableList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        tollRateTableViewModel.tollTable.observe(viewLifecycleOwner, Observer {tableResource ->
            tableResource.data?.let {

                adapter.submitList(it.rows)
                if (it.rows.isEmpty() && tableResource.status != Status.LOADING) {
                    // binding.emptyListView.visibility = View.VISIBLE
                    binding.tollTableList.visibility = View.GONE
                } else {
                    // binding.emptyListView.visibility = View.GONE
                    binding.tollTableList.visibility = View.VISIBLE
                }
            }

            spinner = binding.root.findViewById(R.id.tollSpinner)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (tollRateTableViewModel.route.value?.route == 50978) {
                        tollRateTableViewModel.setRoute(50983)
                    } else {
                        tollRateTableViewModel.setRoute(50978)
                    }
                }
            }
        })

        return dataBinding.root
    }

    abstract fun getRoute(): Int
    abstract fun getInfoLinkText(): String
    abstract fun getInfoLinkURL(): String

}