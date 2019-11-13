package gov.wa.wsdot.android.wsdot.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.CameraItemBinding
import gov.wa.wsdot.android.wsdot.databinding.TopicItemBinding
import gov.wa.wsdot.android.wsdot.databinding.TopicItemBindingImpl
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class NotificationsListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val subscribeClickCallback: ((NotificationTopic, Boolean) -> Unit)?
) : DataBoundListAdapter<NotificationTopic, TopicItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<NotificationTopic>() {
        override fun areItemsTheSame(oldItem: NotificationTopic, newItem: NotificationTopic): Boolean {
            return oldItem.topic == newItem.topic
        }
        override fun areContentsTheSame(oldItem: NotificationTopic, newItem: NotificationTopic): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.localCacheDate == newItem.localCacheDate
                    && oldItem.category == newItem.category
                    && oldItem.subscribed == newItem.subscribed
        }
    }
) {

    override fun createBinding(parent: ViewGroup): TopicItemBinding {

        val binding = DataBindingUtil.inflate<TopicItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.topic_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<CheckBox>(R.id.sub_checkbox).setOnCheckedChangeListener { _, isChecked ->
            binding.topicItem?.let {
                subscribeClickCallback?.invoke(it, isChecked)
            }
        }

        return binding
    }

    override fun bind(binding: TopicItemBinding, item: NotificationTopic, position: Int) {
        binding.topicItem = item
    }
}