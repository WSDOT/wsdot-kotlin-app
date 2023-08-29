package gov.wa.wsdot.android.wsdot.ui.notifications.recyclerview

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.*
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import kotlinx.android.synthetic.main.topic_item.view.*

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
                return TopicViewHolder(
                    createHeaderBinding(parent)
                )
            }
            ITEM_TYPE_TOPIC -> {
                return TopicViewHolder(
                    createTopicBinding(parent)
                )
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

                 if (!NotificationManagerCompat.from(parent.context).areNotificationsEnabled() && binding.root.findViewById<CheckBox>(R.id.sub_checkbox).isPressed) {

                     binding.root.sub_checkbox.isChecked = !binding.root.sub_checkbox.isChecked
                     subscribeClickCallback?.invoke(it, !isChecked)

                     val notificationMessage =
                        SpannableString("Please allow notifications from Settings")
                     val alert: AlertDialog = AlertDialog.Builder(parent.context)
                        .setTitle("Turn On Notifications")
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                        .setPositiveButton("Open Settings") { _, _ ->
                            val intent: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, parent.context.packageName)
                            } else {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.setData(Uri.parse("package:" + parent.context.applicationContext.packageName))
                            }
                            parent.context.startActivity(intent)
                        }
                        .setMessage(Html.fromHtml(notificationMessage.toString()))
                        .create()
                        alert.show()

                }
                else {
                    subscribeClickCallback?.invoke(it, isChecked)

                }
            }
        }

        return binding
    }

}
