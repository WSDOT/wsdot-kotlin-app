package gov.wa.wsdot.android.wsdot.ui.notifications

import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.databinding.*

/**
 *  A view holder for a favorites item.
 *  Must have a binding for all possible types of favorites.
 */
class TopicViewHolder : RecyclerView.ViewHolder {

    lateinit var headerBinding: HeaderItemBinding
    lateinit var topicItemBinding: TopicItemBinding

    constructor(binding: HeaderItemBinding) : super(binding.root) {
        headerBinding = binding
    }

    constructor(binding: TopicItemBinding) : super(binding.root) {
        topicItemBinding = binding
    }

}