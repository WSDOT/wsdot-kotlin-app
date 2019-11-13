package gov.wa.wsdot.android.wsdot.ui.notifications

import android.util.Log
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingComponent
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.*
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import java.lang.Exception

class TopicListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val subscribeClickCallback: ((NotificationTopic, Boolean) -> Unit)?
) : RecyclerView.Adapter<TopicViewHolder>() {

    private var data = HashMap<String, List<NotificationTopic>>()

    companion object ViewType {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_TOPIC = 1
    }

    fun setTopics(data: HashMap<String, List<NotificationTopic>>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {

        when (viewType) {
            ITEM_TYPE_HEADER -> {
                return TopicViewHolder(createHeaderBinding(parent))
            }
            ITEM_TYPE_TOPIC -> {
                return TopicViewHolder(createTopicBinding(parent))
            }
        }

        // TODO: crash reporting
        Log.e("debug", "throwing exception in onCreateViewHolder")
        throw Exception()

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is String) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_TOPIC
        }
    }

    /**
     *  Get the item at the given position in the entire adapter.
     */
    private fun getItem(position: Int): Any {

        var currentPos = position

        for (key in data.keys) {

            val size = data[key]!!.size + (if (data[key]!!.isEmpty()) 0 else 1)

            if (currentPos == 0 && size > 0) {
                return key
            }

            if (currentPos < size) {
                return data[key]!![currentPos - 1]
            }

            currentPos -= size
        }

        // TODO: crash reporting
        Log.e("debug", "throwing exception in getItem")
        throw Exception()

    }

    override fun getItemCount(): Int {
        var size = 0

        for (key in data.keys) {
            size += data[key]!!.size + (if (data[key]!!.isEmpty()) 0 else 1) // +1 for header
        }

        return size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_TYPE_HEADER -> {
                holder.headerBinding.title = getItem(position) as String
                holder.headerBinding.executePendingBindings()
            }
            ITEM_TYPE_TOPIC -> {
                holder.topicItemBinding.topicItem = getItem(position) as NotificationTopic
                holder.topicItemBinding.executePendingBindings()
            }
        }
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

    private fun createTopicBinding(parent: ViewGroup): TopicItemBinding {
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

}
