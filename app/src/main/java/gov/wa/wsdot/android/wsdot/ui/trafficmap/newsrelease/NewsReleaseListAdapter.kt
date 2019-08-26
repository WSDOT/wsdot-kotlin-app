package gov.wa.wsdot.android.wsdot.ui.trafficmap.newsrelease

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.NewsItemBinding
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsRelease
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [NewsRelease] class.
 */
class NewsReleaseListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val newsClickCallback: ((NewsRelease) -> Unit)? // ClickCallback for item in the adapter
) : DataBoundListAdapter<NewsRelease, NewsItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<NewsRelease>() {

        override fun areItemsTheSame(oldItem: NewsRelease, newItem: NewsRelease): Boolean {
            return oldItem.link == newItem.link
        }

        override fun areContentsTheSame(oldItem: NewsRelease, newItem:NewsRelease): Boolean {
            return oldItem.description == newItem.description
                    && oldItem.pubdate.time == newItem.pubdate.time
                    && oldItem.title == newItem.title
                    && oldItem.description == newItem.description
                    && oldItem.link == newItem.link
        }
    }
) {

    override fun createBinding(parent: ViewGroup): NewsItemBinding {

        val binding = DataBindingUtil.inflate<NewsItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.news_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.newsItem?.let {
                newsClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: NewsItemBinding, item: NewsRelease, position: Int) {
        binding.newsItem = item
    }

}