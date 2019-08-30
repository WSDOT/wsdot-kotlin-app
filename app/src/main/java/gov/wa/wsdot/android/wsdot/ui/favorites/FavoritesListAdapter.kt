package gov.wa.wsdot.android.wsdot.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraItemBinding
import gov.wa.wsdot.android.wsdot.databinding.TravelTimeItemBinding
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundViewHolder
import gov.wa.wsdot.android.wsdot.ui.favorites.items.FavoriteItems
import gov.wa.wsdot.android.wsdot.ui.favorites.items.FavoriteViewHolder
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import java.lang.Exception

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding
</V></T> */

class FavoritesListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val travelTimeClickCallback: ((TravelTime) -> Unit)?, // ClickCallback for item in the adapter
    private val cameraClickCallback: ((Camera) -> Unit)?
) : RecyclerView.Adapter<FavoriteViewHolder>() {


    companion object ViewTypes {
        const val ITEM_TYPE_TRAVEL_TIME = 0
        const val ITEM_TYPE_CAMERA = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

        when (viewType) {
            ITEM_TYPE_TRAVEL_TIME -> { return FavoriteViewHolder(createTravelTimeBinding(parent)) }
            ITEM_TYPE_CAMERA -> { return FavoriteViewHolder(createCameraBinding(parent)) }
        }

        throw Exception()

    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_TYPE_TRAVEL_TIME -> {
                holder.travelTimeItemBinding.travelTime = getTravelTimeItem(position)
                holder.travelTimeItemBinding.executePendingBindings()
            }
            ITEM_TYPE_CAMERA -> {
                holder.cameraItemBinding.camera = getCameraItem(position)
                holder.cameraItemBinding.executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)

    }

    private fun createTravelTimeBinding(parent: ViewGroup): TravelTimeItemBinding {

        val binding = DataBindingUtil.inflate<TravelTimeItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.travel_time_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.travelTime?.let {
                travelTimeClickCallback?.invoke(it)
            }
        }

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



}
