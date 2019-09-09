package gov.wa.wsdot.android.wsdot.ui.favorites

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FavoritesListFragmentBinding
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter.ViewType.ITEM_TYPE_CAMERA
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter.ViewType.ITEM_TYPE_FERRY
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter.ViewType.ITEM_TYPE_HEADER
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter.ViewType.ITEM_TYPE_MOUNTAIN_PASS
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter.ViewType.ITEM_TYPE_TRAVEL_TIME


class FavoritesFragment : DaggerFragment(), AdapterDataSetChangedListener, Injectable  {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var favoritesListViewModel: FavoritesListViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FavoritesListFragmentBinding>()

    private var adapter by autoCleared<FavoritesListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FavoritesListViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<FavoritesListFragmentBinding>(
            inflater,
            R.layout.favorites_list_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                favoritesListViewModel.refresh()
            }
        }

        dataBinding.viewModel = favoritesListViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).disableAds()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = FavoritesListAdapter(
            dataBindingComponent,
            appExecutors,
            this,
            getOrderedViewTypes(),
            { camera ->
                navigateToCamera(camera)
            },
            { ferrySchedule ->
                navigateToSchedule(ferrySchedule)
            },
            { mountainPass ->
                navigateToMountainPass(mountainPass)
            })

        this.adapter = adapter

        addFavoriteItemTouchHelper(this.adapter, binding.favoritesList)

        binding.favoritesList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.favoritesList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        favoritesListViewModel.favoriteTravelTimes.observe(viewLifecycleOwner, Observer { favItems ->
            favItems?.let {
                adapter.setTravelTimes(it)
            }
        })

        favoritesListViewModel.favoriteCameras.observe(viewLifecycleOwner, Observer { favItems ->
            favItems?.let {
                adapter.setCameras(it)
            }
        })

        favoritesListViewModel.favoriteFerrySchedules.observe(viewLifecycleOwner, Observer { favItems ->
            favItems?.let {
                adapter.setFerrySchedules(it)
            }
        })

        favoritesListViewModel.favoriteMountainPasses.observe(viewLifecycleOwner, Observer { favItems ->
            favItems?.let {
                adapter.setMountainPasses(it)
            }
        })

    }

    override fun onDataSetChanged() {
        shouldShowEmptyFavorites(binding)
    }

    private fun getOrderedViewTypes(): List<Int> {

        val orderedViewTypes = mutableListOf<Int>()

        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        orderedViewTypes.add(settings.getInt(resources.getString(R.string.favorites_one), ITEM_TYPE_FERRY))
        orderedViewTypes.add(settings.getInt(resources.getString(R.string.favorites_two), ITEM_TYPE_MOUNTAIN_PASS))
        orderedViewTypes.add(settings.getInt(resources.getString(R.string.favorites_three), ITEM_TYPE_TRAVEL_TIME))
        orderedViewTypes.add(settings.getInt(resources.getString(R.string.favorites_four), ITEM_TYPE_CAMERA))

        return orderedViewTypes
    }

    private fun addFavoriteItemTouchHelper(adapter: FavoritesListAdapter, recyclerView: RecyclerView) {

        // Add swipe dismiss to favorites list items.
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


                    val icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_remove_favorite,
                        null
                    )
                    val background = ColorDrawable(Color.parseColor("#707070"))

                    val backgroundCornerOffset = 10
                    val iconMargin = 48
                    val iconTop =
                        viewHolder.itemView.top + (viewHolder.itemView.height - icon!!.intrinsicHeight) / 2
                    val iconBottom = iconTop + icon.intrinsicHeight

                    when {
                        dX < 0 -> { // Left swipe
                            val iconLeft =
                                viewHolder.itemView.right - iconMargin - icon.intrinsicWidth
                            val iconRight = viewHolder.itemView.right - iconMargin
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                            background.setBounds(
                                viewHolder.itemView.right + dX.toInt() - backgroundCornerOffset,
                                viewHolder.itemView.top + 8,
                                viewHolder.itemView.right,
                                viewHolder.itemView.bottom - 8
                            )
                        }
                        else -> background.setBounds(0, 0, 0, 0)
                    }
                    background.draw(c)
                    icon.draw(c)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return if (adapter.getItemViewType(viewHolder.adapterPosition) == ITEM_TYPE_HEADER) 0 else super.getSwipeDirs(
                        recyclerView,
                        viewHolder
                    )
                }

                override fun onSwiped(holder: RecyclerView.ViewHolder, swipeDir: Int) {

                    val itemId: String?
                    val viewType = adapter.getItemViewType(holder.adapterPosition)

                    //get the camera id or tag for the item being removed.
                    when (viewType) {
                        ITEM_TYPE_CAMERA -> {
                            val camera = adapter.getItem(holder.adapterPosition) as Camera
                            itemId = camera.cameraId.toString()
                            favoritesListViewModel.updateFavoriteCamera(camera.cameraId, false)
                        }
                        ITEM_TYPE_FERRY -> {
                            val schedule = adapter.getItem(holder.adapterPosition) as FerrySchedule
                            itemId = schedule.routeId.toString()
                            favoritesListViewModel.updateFavoriteFerrySchedule(schedule.routeId, false)
                        }
                        ITEM_TYPE_TRAVEL_TIME -> {
                            val travelTime = adapter.getItem(holder.adapterPosition) as TravelTime
                            itemId = travelTime.travelTimeId.toString()
                            favoritesListViewModel.updateFavoriteTravelTime(travelTime.travelTimeId, false)
                        }
                        ITEM_TYPE_MOUNTAIN_PASS -> {
                            val pass = adapter.getItem(holder.adapterPosition) as MountainPass
                            itemId = pass.passId.toString()
                            favoritesListViewModel.updateFavoritePass(pass.passId, false)
                        }

                        else -> itemId = null
                    }

                    // Display snack bar with undo button

                    val snackbar = Snackbar.make(recyclerView, "Removed Favorite", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO") {
                        when (viewType) {
                            ITEM_TYPE_CAMERA -> {
                                favoritesListViewModel.updateFavoriteCamera(itemId!!.toInt(), true)
                            }
                            ITEM_TYPE_FERRY -> {
                                favoritesListViewModel.updateFavoriteFerrySchedule(itemId!!.toInt(), true)
                            }
                            ITEM_TYPE_TRAVEL_TIME -> {
                                favoritesListViewModel.updateFavoriteTravelTime(itemId!!.toInt(), true)
                            }
                            ITEM_TYPE_MOUNTAIN_PASS -> {
                                favoritesListViewModel.updateFavoritePass(itemId!!.toInt(), true)
                            }

                        }

                    }
                    snackbar.show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }


    private fun navigateToCamera(camera: Camera){
        val action = NavGraphDirections.actionGlobalNavCameraFragment(camera.cameraId, camera.title)
        (activity as MainActivity).enableAds(resources.getString(R.string.ad_target_traffic))
        findNavController().navigate(action)
    }

    private fun navigateToSchedule(schedule: FerrySchedule) {
        val action = NavGraphDirections.actionGlobalNavFerriesRouteFragment(schedule.routeId, schedule.description)
        (activity as MainActivity).enableAds(resources.getString(R.string.ad_target_ferries))
        findNavController().navigate(action)
    }

    private fun navigateToMountainPass(pass: MountainPass) {
        val action = NavGraphDirections.actionGlobalNavMountainPassReportFragment(pass.passId, pass.passName)
        (activity as MainActivity).enableAds(resources.getString(R.string.ad_target_passes))
        findNavController().navigate(action)
    }

    private fun shouldShowEmptyFavorites(binding: FavoritesListFragmentBinding) {

        Log.e("Debug", String.format("list size: %d", adapter.getNumItems()))

        if (adapter.getNumItems() == 0) {
            binding.emptyListView.visibility = View.VISIBLE
            binding.favoritesList.visibility = View.GONE
        } else {
            binding.emptyListView.visibility = View.GONE
            binding.favoritesList.visibility = View.VISIBLE
        }
    }

}