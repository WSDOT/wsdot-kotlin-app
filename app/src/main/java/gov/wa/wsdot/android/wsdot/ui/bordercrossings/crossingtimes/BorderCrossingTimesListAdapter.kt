package gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.BorderCrossingItemBinding
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.BorderCrossingDiffCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class BorderCrossingTimesListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val favoriteClickCallback: ((BorderCrossing) -> Unit)?
) : DataBoundListAdapter<BorderCrossing, BorderCrossingItemBinding>(
    appExecutors = appExecutors,
    diffCallback = BorderCrossingDiffCallback()
) {

    override fun createBinding(parent: ViewGroup): BorderCrossingItemBinding {

        val binding = DataBindingUtil.inflate<BorderCrossingItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.border_crossing_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<ImageButton>(R.id.favorite_button).setOnClickListener {
            binding.crossing?.let {
                favoriteClickCallback?.invoke(it)
            }
        }

        binding.directionView.visibility = INVISIBLE

        return binding
    }

    override fun bind(binding: BorderCrossingItemBinding, item: BorderCrossing, position: Int) {
        binding.crossing = item
    }
}