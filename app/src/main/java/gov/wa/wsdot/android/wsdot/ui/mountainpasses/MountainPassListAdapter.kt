package gov.wa.wsdot.android.wsdot.ui.mountainpasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.MountainPassItemBinding
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [MountainPass] class.
 */
class MountainPassListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val passClickCallback: ((MountainPass) -> Unit)?, // ClickCallback for item in the adapter
    private val favoriteClickCallback: ((MountainPass) -> Unit)?
) : DataBoundListAdapter<MountainPass, MountainPassItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<MountainPass>() {
        override fun areItemsTheSame(oldItem: MountainPass, newItem: MountainPass): Boolean {
            return oldItem.passId == newItem.passId
        }

        override fun areContentsTheSame(oldItem: MountainPass, newItem: MountainPass): Boolean {
            return oldItem.passName == newItem.passName
                    && oldItem.forecasts == newItem.forecasts
                    && oldItem.weatherCondition == newItem.weatherCondition
                    && oldItem.serverCacheDate.time == newItem.serverCacheDate.time
                    && oldItem.favorite == newItem.favorite
        }
    }
) {

    override fun createBinding(parent: ViewGroup): MountainPassItemBinding {

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

        binding.root.findViewById<ImageButton>(R.id.favorite_button).setOnClickListener {
            binding.pass?.let {
                favoriteClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: MountainPassItemBinding, item: MountainPass, position: Int) {
        binding.pass = item
    }
}