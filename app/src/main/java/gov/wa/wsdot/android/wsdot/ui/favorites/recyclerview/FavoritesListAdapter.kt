package gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.CameraDiffCallback
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.FerryScheduleDiffCallback
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.MountainPassDiffCallback
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.TravelTimeDiffCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import java.lang.Exception


/**
 * A RecyclerView adapter that uses Data Binding & DiffUtil for the Favorites Section.
 *
 * Has multiple AsyncListDiffers for each data type in the list.
 *
 */

class FavoritesListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    viewTypes: List<Int>,
    private val cameraClickCallback: ((Camera) -> Unit)?,
    private val scheduleClickCallback: ((FerrySchedule) -> Unit)?,
    private val passClickCallback: ((MountainPass) -> Unit)?
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val travelTimesDiffer: AsyncListDiffer<TravelTime> = AsyncListDiffer<TravelTime>(
        FavoritesListUpdateCallback(
            this,
            ITEM_TYPE_TRAVEL_TIME,
            1
        ),
        AsyncDifferConfig.Builder<TravelTime>(TravelTimeDiffCallback())
            .setBackgroundThreadExecutor(appExecutors.diskIO())
            .build()
    )

    private val camerasDiffer = AsyncListDiffer<Camera>(
        FavoritesListUpdateCallback(
            this,
            ITEM_TYPE_CAMERA,
            1
        ),
        AsyncDifferConfig.Builder<Camera>(CameraDiffCallback())
            .setBackgroundThreadExecutor(appExecutors.diskIO())
            .build()
    )

    private val ferryScheduleDiffer = AsyncListDiffer<FerrySchedule>(
        FavoritesListUpdateCallback(
            this,
            ITEM_TYPE_FERRY,
            1
        ),
        AsyncDifferConfig.Builder<FerrySchedule>(FerryScheduleDiffCallback())
            .setBackgroundThreadExecutor(appExecutors.diskIO())
            .build()
    )

    private val mountainPassesDiffer = AsyncListDiffer<MountainPass>(
        FavoritesListUpdateCallback(
            this,
            ITEM_TYPE_MOUNTAIN_PASS,
            1
        ),
        AsyncDifferConfig.Builder<MountainPass>(MountainPassDiffCallback())
            .setBackgroundThreadExecutor(appExecutors.diskIO())
            .build()
    )


    // sort order for each view type
    private var orderedViewTypes = viewTypes

    companion object ViewType {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_TRAVEL_TIME = 1
        const val ITEM_TYPE_CAMERA = 2
        const val ITEM_TYPE_FERRY = 3
        const val ITEM_TYPE_MOUNTAIN_PASS = 4
    }

    private var headers = object : LinkedHashMap<Int, String>() {
        init {
            put(ITEM_TYPE_CAMERA, "Cameras")
            put(ITEM_TYPE_TRAVEL_TIME, "Travel Times")
            put(ITEM_TYPE_FERRY, "Ferry Schedules")
            put(ITEM_TYPE_MOUNTAIN_PASS, "Mountain Passes")
        }
    }

    fun setOrderedViewTypes(newOrderedViewTypes: List<Int>){
        this.orderedViewTypes = newOrderedViewTypes
    }

    fun setCameras(data: List<Camera>) {
        camerasDiffer.submitList(data)
    }

    fun setTravelTimes(data: List<TravelTime>){
        travelTimesDiffer.submitList(data)
    }

    fun setFerrySchedules(data: List<FerrySchedule>){
        ferryScheduleDiffer.submitList(data)
    }

    fun setMountainPasses(data: List<MountainPass>) {
        mountainPassesDiffer.submitList(data)
    }

    /**
     * Returns the number of items for a given item_type
     */
    fun getNumItemsInSection(type: Int): Int {
        return when(type) {
            ITEM_TYPE_TRAVEL_TIME -> { getTravelTimesSize() }
            ITEM_TYPE_CAMERA -> { getCamerasSize() }
            ITEM_TYPE_FERRY -> { getFerrySchedulesSize() }
            ITEM_TYPE_MOUNTAIN_PASS -> { getMountainPassesSize() }
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

        when (viewType) {
            ITEM_TYPE_HEADER -> { return FavoriteViewHolder(
                createHeaderBinding(parent)
            )
            }
            ITEM_TYPE_TRAVEL_TIME -> { return FavoriteViewHolder(
                createTravelTimeBinding(parent)
            )
            }
            ITEM_TYPE_CAMERA -> { return FavoriteViewHolder(
                createCameraBinding(parent)
            )
            }
            ITEM_TYPE_FERRY -> { return FavoriteViewHolder(
                createFerryScheduleBinding(parent)
            )
            }
            ITEM_TYPE_MOUNTAIN_PASS -> { return FavoriteViewHolder(
                createMountainPassBinding(parent)
            )
            }
        }

        // TODO: crash reporting
        Log.e("debug", "throwing exception in onCreateViewHolder")
        throw Exception()

    }

    override fun getItemViewType(position: Int): Int {

        var currentPos = position

        for (viewType in orderedViewTypes) {

            var size = 0

            when(viewType) {
                ITEM_TYPE_CAMERA -> { size = getCamerasSize() }
                ITEM_TYPE_TRAVEL_TIME -> { size = getTravelTimesSize() }
                ITEM_TYPE_FERRY -> { size = getFerrySchedulesSize() }
                ITEM_TYPE_MOUNTAIN_PASS -> { size = getMountainPassesSize() }
            }

            if (currentPos == 0 && size > 0) return ITEM_TYPE_HEADER
            if (currentPos < size) return viewType

            currentPos -= size

        }

        // TODO: crash reporting
        Log.e("debug", "throwing exception in getItemViewType")
        throw Exception()

    }

    /**
     *  Sums items before the start of the item type section we want so we can
     *  get the position of an item in the entire adapter.
     */

    fun getPositionInAdapterForItem(posInDataList: Int, itemType: Int): Int {

        var pos = posInDataList

        for (type in orderedViewTypes) {

            if (type == itemType) {
                return pos
            } else {
                when(type) {
                    ITEM_TYPE_CAMERA -> pos += getCamerasSize()
                    ITEM_TYPE_FERRY -> pos += getFerrySchedulesSize()
                    ITEM_TYPE_MOUNTAIN_PASS -> pos += getMountainPassesSize()
                    ITEM_TYPE_TRAVEL_TIME -> pos += getTravelTimesSize()
                }
            }
        }
        return pos

    }


    /**
     *  Get the item at the given position in the entire adapter.
     */
    fun getItem(position: Int): Any {

        var currentPos = position

        for (viewType in orderedViewTypes) {

            var size = 0

            when(viewType) {
                ITEM_TYPE_CAMERA -> { size = getCamerasSize() }
                ITEM_TYPE_TRAVEL_TIME -> { size = getTravelTimesSize() }
                ITEM_TYPE_FERRY -> { size = getFerrySchedulesSize() }
                ITEM_TYPE_MOUNTAIN_PASS -> { size = getMountainPassesSize() }
            }

            if (currentPos == 0 && size > 0) {
                headers[viewType]?.let {
                    return it
                }
                throw Exception()
            }

            if (currentPos < size) {
                when(viewType) {
                    // - 1 for header
                    ITEM_TYPE_CAMERA -> { return camerasDiffer.currentList[currentPos - 1] }
                    ITEM_TYPE_TRAVEL_TIME -> { return travelTimesDiffer.currentList[currentPos - 1] }
                    ITEM_TYPE_FERRY -> { return ferryScheduleDiffer.currentList[currentPos - 1] }
                    ITEM_TYPE_MOUNTAIN_PASS -> { return mountainPassesDiffer.currentList[currentPos - 1] }
                }
            }

            currentPos -= size

        }

        // TODO: crash reporting
        Log.e("debug", "throwing exception in getItem")
        throw Exception()

    }

    override fun getItemCount(): Int {
        return (
                    getCamerasSize() +
                    getTravelTimesSize() +
                    getFerrySchedulesSize() +
                    getMountainPassesSize()
                )
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {

        when (holder.itemViewType) {

            ITEM_TYPE_HEADER -> {
                holder.headerBinding.title = getItem(position) as String
                holder.headerBinding.executePendingBindings()
            }
            ITEM_TYPE_TRAVEL_TIME -> {
                holder.travelTimeItemBinding.travelTime = getItem(position) as TravelTime
                holder.travelTimeItemBinding.executePendingBindings()
            }
            ITEM_TYPE_CAMERA -> {
                holder.cameraItemBinding.camera = getItem(position) as Camera
                holder.cameraItemBinding.executePendingBindings()
            }
            ITEM_TYPE_FERRY -> {
                holder.ferryScheduleItemBinding.schedule = getItem(position) as FerrySchedule
                holder.ferryScheduleItemBinding.executePendingBindings()
            }
            ITEM_TYPE_MOUNTAIN_PASS -> {
                holder.mountainPassItemBinding.pass = getItem(position) as MountainPass
                holder.mountainPassItemBinding.executePendingBindings()
            }
        }
    }

    /**
     *  Methods for calculation the size of each section.
     *  Take into account a header that only shows when there are items in a section.
     */
    private fun getCamerasSize(): Int {
        return camerasDiffer.currentList.size + (if (camerasDiffer.currentList.isNotEmpty()) 1 else 0)
    }

    private fun getTravelTimesSize(): Int {
        return travelTimesDiffer.currentList.size + (if (travelTimesDiffer.currentList.isNotEmpty()) 1 else 0)
    }

    private fun getFerrySchedulesSize(): Int {
        return ferryScheduleDiffer.currentList.size + (if (ferryScheduleDiffer.currentList.isNotEmpty()) 1 else 0)
    }

    private fun getMountainPassesSize(): Int {
        return mountainPassesDiffer.currentList.size + (if (mountainPassesDiffer.currentList.isNotEmpty()) 1 else 0)
    }

    // Binding Methods

    private fun createHeaderBinding(parent: ViewGroup): HeaderItemBinding {

        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.header_item,
            parent,
            false,
            dataBindingComponent
        )

    }

    private fun createTravelTimeBinding(parent: ViewGroup): TravelTimeItemBinding {

        val binding = DataBindingUtil.inflate<TravelTimeItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.travel_time_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<ImageButton>(R.id.favorite_button).visibility = GONE

        return binding
    }

    private fun createCameraBinding(parent: ViewGroup): CameraItemBinding {

        val binding = DataBindingUtil.inflate<CameraItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.camera_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.cameraView).setOnClickListener {
            binding.camera?.let {
                cameraClickCallback?.invoke(it)
            }
        }

        binding.root.findViewById<ImageButton>(R.id.favorite_button).visibility = GONE

        return binding
    }

    private fun createFerryScheduleBinding(parent: ViewGroup): FerryScheduleItemBinding {

        val binding = DataBindingUtil.inflate<FerryScheduleItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.ferry_schedule_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.schedule?.let {
                scheduleClickCallback?.invoke(it)
            }
        }

        binding.root.findViewById<ImageButton>(R.id.favorite_button).visibility = GONE

        return binding
    }

    private fun createMountainPassBinding(parent: ViewGroup): MountainPassItemBinding {

        val binding = DataBindingUtil.inflate<MountainPassItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.mountain_pass_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.pass?.let {
                passClickCallback?.invoke(it)
            }
        }

        binding.root.findViewById<ImageButton>(R.id.favorite_button).visibility = GONE

        return binding
    }

}
