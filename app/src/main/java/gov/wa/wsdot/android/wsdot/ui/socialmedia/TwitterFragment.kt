package gov.wa.wsdot.android.wsdot.ui.socialmedia

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TwitterFragmentBinding
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.util.network.Status

class TwitterFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var tweetViewModel: TwitterViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<TwitterFragmentBinding>()

    private var adapter by autoCleared<TwitterListAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tweetViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(TwitterViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<TwitterFragmentBinding>(
            inflater,
            R.layout.twitter_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                tweetViewModel.refresh()
            }
        }

        dataBinding.viewModel = tweetViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = TwitterListAdapter(dataBindingComponent, appExecutors)
        { tweet -> openTweetLink(tweet) }

        this.adapter = adapter

        binding.cameraList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.cameraList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        tweetViewModel.tweets.observe(viewLifecycleOwner, Observer { tweetResource ->
            if (tweetResource.data != null) {
                adapter.submitList(tweetResource.data)
            } else {
                adapter.submitList(emptyList())
            }

            if (tweetResource.status == Status.ERROR) {
                Toast.makeText(
                    context,
                    getString(R.string.loading_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun openTweetLink(tweet: Tweet){

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                getString(
                    R.string.twitter_status_url,
                    tweet.userId,
                    tweet.tweetId)))
        startActivity(intent)
    }
}