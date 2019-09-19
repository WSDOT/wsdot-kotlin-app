package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TollSignItemBinding
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.TollSignDiffCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class TollSignsListAdapter (
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val favoriteClickCallback: ((TollSign) -> Unit)?
) : DataBoundListAdapter<TollSign, TollSignItemBinding>(
    appExecutors = appExecutors,
    diffCallback = TollSignDiffCallback()
) {

    override fun createBinding(parent: ViewGroup): TollSignItemBinding {

        val binding = DataBindingUtil.inflate<TollSignItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.toll_sign_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<ImageButton>(R.id.favorite_button).setOnClickListener {
            binding.tollSign?.let {
                favoriteClickCallback?.invoke(it)
            }
        }

        return binding

    }

    override fun bind(binding: TollSignItemBinding, item: TollSign, position: Int) {
        binding.tollSign = item
    }
}