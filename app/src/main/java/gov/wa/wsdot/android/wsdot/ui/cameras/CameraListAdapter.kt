package gov.wa.wsdot.android.wsdot.ui.cameras

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraItemBinding
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [Camera]] class.
 */
class CameraListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val cameraClickCallback: ((Camera) -> Unit)?, // ClickCallback for item in the adapter
    private val favoriteClickCallback: ((Camera) -> Unit)?
) : DataBoundListAdapter<Camera, CameraItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Camera>() {
        override fun areItemsTheSame(oldItem: Camera, newItem: Camera): Boolean {
            return false // TODO
        }

        override fun areContentsTheSame(oldItem: Camera, newItem: Camera): Boolean {
            return false // TODO
        }
    }
) {

    override fun createBinding(parent: ViewGroup): CameraItemBinding {

        val binding = DataBindingUtil.inflate<CameraItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.camera_item,
            parent,
            false,
            dataBindingComponent
        )
/*
        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.camera?.let {
                cameraClickCallback?.invoke(it)
            }
        }

        binding.root.findViewById<ImageButton>(R.id.favorite_button).setOnClickListener {
            binding.camera?.let {
                favoriteClickCallback?.invoke(it)
            }
        }
*/
        return binding
    }

    override fun bind(binding: CameraItemBinding, item: Camera, position: Int) {
        binding.camera = item
    }
}