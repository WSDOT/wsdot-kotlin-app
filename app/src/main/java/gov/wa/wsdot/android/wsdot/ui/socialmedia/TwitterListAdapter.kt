package gov.wa.wsdot.android.wsdot.ui.socialmedia

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TweetItemBinding
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class TwitterListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val tweetClickCallback: ((Tweet) -> Unit)?
) : DataBoundListAdapter<Tweet, TweetItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Tweet>() {
        override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.tweetId == newItem.tweetId
        }
        override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.text == newItem.text
                    && oldItem.tweetId == newItem.tweetId
                    && oldItem.createdAt == newItem.createdAt
        }
    }
) {

    override fun createBinding(parent: ViewGroup): TweetItemBinding {

        val binding = DataBindingUtil.inflate<TweetItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.tweet_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.tweet?.let {
                tweetClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: TweetItemBinding, item: Tweet, position: Int) {
        binding.tweet = item
    }
}