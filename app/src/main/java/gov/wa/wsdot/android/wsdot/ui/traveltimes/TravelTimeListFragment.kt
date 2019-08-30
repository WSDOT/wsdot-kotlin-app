package gov.wa.wsdot.android.wsdot.ui.traveltimes

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.core.internal.view.SupportMenuItem.SHOW_AS_ACTION_ALWAYS
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeListFragmentBinding
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.util.network.Status
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


class TravelTimeListFragment : DaggerFragment(), Injectable, SearchView.OnQueryTextListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var travelTimeListViewModel: TravelTimeListViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<TravelTimeListFragmentBinding>()

    private var adapter by autoCleared<TravelTimeListAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        travelTimeListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(TravelTimeListViewModel::class.java)

        travelTimeListViewModel.setTravelTimeQuery("")

        val dataBinding = DataBindingUtil.inflate<TravelTimeListFragmentBinding>(
            inflater,
            R.layout.travel_time_list_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                travelTimeListViewModel.refresh()
            }
        }

        dataBinding.viewModel = travelTimeListViewModel

        dataBinding.searchView.setOnQueryTextListener(this)

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = TravelTimeListAdapter(dataBindingComponent, appExecutors,
            { travelTime -> navigateToTravelTime(travelTime) },
            { travelTimeListViewModel.updateFavorite(travelTimeId = it.travelTimeId, isFavorite = !it.favorite) })

        this.adapter = adapter

        binding.travelTimeList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.travelTimeList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        travelTimeListViewModel.travelTimes.observe(viewLifecycleOwner, Observer { travelTimesResourse ->
            if (travelTimesResourse.data != null) {
                adapter.submitList(travelTimesResourse.data)
                if (travelTimesResourse.data.isEmpty() && travelTimesResourse.status != Status.LOADING) {
                    binding.emptyListView.visibility = VISIBLE
                    binding.travelTimeList.visibility = GONE
                } else {
                    binding.emptyListView.visibility = GONE
                    binding.travelTimeList.visibility = VISIBLE
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        activity?.let { activityValue ->
            view?.let {viewValue ->
                val inputManager = activityValue.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(viewValue.windowToken, HIDE_NOT_ALWAYS)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            travelTimeListViewModel.setTravelTimeQuery(it)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            travelTimeListViewModel.setTravelTimeQuery(it)

        }
        return true
    }

    // uses Safe Args to pass data https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
    private fun navigateToTravelTime(travelTime: TravelTime){
      //  val action = NavGraphDirections.actionGlobalNavTravelTimeFragment(travelTime.travelTimeId, travelTime.title)
      //  findNavController().navigate(action)
    }
}