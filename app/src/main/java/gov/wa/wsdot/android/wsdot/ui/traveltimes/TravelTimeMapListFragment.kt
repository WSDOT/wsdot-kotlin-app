package gov.wa.wsdot.android.wsdot.ui.traveltimes

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.google.android.gms.maps.model.LatLng
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeMapListFragmentBinding
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.MainActivity

class TravelTimeMapListFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var travelTimeListViewModel: TravelTimeListViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    val args: TravelTimeMapListFragmentArgs by navArgs()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<TravelTimeMapListFragmentBinding>()

    private var adapter by autoCleared<TravelTimeMapListAdapter>()

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
    ): View {

        travelTimeListViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TravelTimeListViewModel::class.java)

        travelTimeListViewModel.setTravelTimeQuery("")

        val dataBinding = DataBindingUtil.inflate<TravelTimeMapListFragmentBinding>(
            inflater,
            R.layout.travel_time_map_list_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                travelTimeListViewModel.refresh()
            }
        }

        dataBinding.viewModel = travelTimeListViewModel

        binding = dataBinding


        val adapter =
            TravelTimeMapListAdapter(
                dataBindingComponent,
                appExecutors
            ) { travelTime ->
                navigateToMap(
                    LatLng(travelTime.startLocationLatitude, travelTime.startLocationLongitude),
                    LatLng(travelTime.endLocationLatitude, travelTime.endLocationLatitude),
                    travelTime
                )
            }

        this.adapter = adapter


        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = TravelTimeMapListAdapter(dataBindingComponent, appExecutors
        ) { travelTime ->
            navigateToMap(
                LatLng(travelTime.startLocationLatitude, travelTime.startLocationLongitude),
                LatLng(travelTime.endLocationLatitude, travelTime.endLocationLongitude),
                travelTime
            )
        }

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

                val list: ArrayList<TravelTime> = ArrayList()
                val routeIds = args.routeIds.toList()

                for (travelTime in travelTimesResourse.data) {

                    if (routeIds.contains(travelTime.travelTimeId)) {
                        list.add(travelTime)
                    }
                }

                adapter.submitList(list.sortedBy{it.title})

                if (travelTimesResourse.data.isEmpty() && travelTimesResourse.status != Status.LOADING) {
                    binding.emptyListView.visibility = VISIBLE
                    binding.travelTimeList.visibility = GONE
                } else {
                    binding.emptyListView.visibility = GONE
                    binding.travelTimeList.visibility = VISIBLE
                }
            }

            if (travelTimesResourse.status == Status.ERROR) {
                binding.emptyListView.visibility = GONE
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