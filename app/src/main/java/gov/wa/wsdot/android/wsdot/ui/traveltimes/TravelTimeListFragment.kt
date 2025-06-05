package gov.wa.wsdot.android.wsdot.ui.traveltimes

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeListFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.model.common.Status
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.google.android.gms.maps.model.LatLng
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class TravelTimeListFragment : DaggerFragment(), Injectable, SearchView.OnQueryTextListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var travelTimeListViewModel: TravelTimeListViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<TravelTimeListFragmentBinding>()

    private var adapter by autoCleared<TravelTimeListAdapter>()

    private lateinit var toast: Toast
    private var isFavorite: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        travelTimeListViewModel = ViewModelProvider(this, viewModelFactory)
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


        val adapter =
            TravelTimeListAdapter(
                dataBindingComponent,
                appExecutors,
                { travelTime ->
                    travelTimeListViewModel.updateFavorite(travelTime.travelTimeId, !travelTime.favorite)
                },
                { travelTime ->
                        navigateToMap(
                            LatLng(travelTime.startLocationLatitude, travelTime.startLocationLongitude),
                            LatLng(travelTime.endLocationLatitude, travelTime.endLocationLatitude),
                            travelTime
                        )



                })

        this.adapter = adapter


        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = TravelTimeListAdapter(dataBindingComponent, appExecutors,
            { travelTime ->
                travelTimeListViewModel.updateFavorite(travelTimeId = travelTime.travelTimeId, isFavorite = !travelTime.favorite)

                isFavorite = travelTime.favorite

                if (this::toast.isInitialized)
                {
                    toast.cancel()
                }

                if (!isFavorite) {
                    toast = Toast.makeText(context, getString(R.string.favorite_added_message), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }
                else {
                    toast = Toast.makeText(context, getString(R.string.favorite_removed_message), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER,0,500)
                    toast.show()
                }

            },
            { travelTime ->
                navigateToMap(
                    LatLng(travelTime.startLocationLatitude, travelTime.startLocationLongitude),
                    LatLng(travelTime.endLocationLatitude, travelTime.endLocationLongitude),
                    travelTime
                )
            }
        )

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.travelTimeList.addItemDecoration(itemDivider)

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
                adapter.submitList(
                    travelTimesResourse.data.asSequence().sortedBy{it.title}
                        .filter {it.travelTimeId != 36} // Bellevue to Lynnwood
                        .filter {it.travelTimeId != 37} // Lynnwood to Bellevue
                        .filter {it.travelTimeId != 68}	// Auburn to Renton
                        .filter {it.travelTimeId != 69}.toList() // Renton to Auburn
                )
                if (travelTimesResourse.data.isEmpty() && travelTimesResourse.status != Status.LOADING) {
                    binding.emptyListView.visibility = VISIBLE
                    binding.travelTimeList.visibility = GONE
                } else {
                    binding.emptyListView.visibility = GONE
                    binding.travelTimeList.visibility = VISIBLE
                }
            }

            if (travelTimesResourse.status == Status.ERROR) {
                binding.emptyListView.visibility = View.GONE
                Toast.makeText(
                    context,
                    getString(R.string.loading_error_message),
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun navigateToMap(startLocation: LatLng, endLocation: LatLng, travelTime: TravelTime){
        val action = NavGraphDirections.actionGlobalNavTravelTimeFragment(
            startLatitude = startLocation.latitude.toString(),
            startLongitude = startLocation.longitude.toString(),
            endLatitude = endLocation.latitude.toString(),
            endLongitude = endLocation.longitude.toString(),
            title = "Travel Time",
            routeId = travelTime.travelTimeId
            )

        findNavController().navigate(action)
    }

}