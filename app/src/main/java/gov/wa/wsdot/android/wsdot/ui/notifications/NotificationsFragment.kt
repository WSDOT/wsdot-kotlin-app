package gov.wa.wsdot.android.wsdot.ui.notifications

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.NotificationsFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.common.callback.RetryCallback
import gov.wa.wsdot.android.wsdot.ui.notifications.recyclerview.TopicListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import javax.inject.Inject

class NotificationsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var notificationsViewModel: NotificationsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<NotificationsFragmentBinding>()

    private var adapter by autoCleared<TopicListAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        notificationsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NotificationsViewModel::class.java)

        val dataBinding = DataBindingUtil.inflate<NotificationsFragmentBinding>(
            inflater,
            R.layout.notifications_fragment,
            container,
            false
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                notificationsViewModel.refresh()
            }
        }

        dataBinding.viewModel = notificationsViewModel

        binding = dataBinding

        // animation
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // pass function to be called on adapter item tap and favorite
        val adapter = TopicListAdapter(
            dataBindingComponent
        )
        { topicItem, isChecked ->

            if (isChecked != topicItem.subscribed) {

                notificationsViewModel.updateSubscription(
                    topicItem.topic,
                    isChecked
                )
                (activity as MainActivity).updateTopicSub(topicItem.topic, isChecked)
            }
        }

        this.adapter = adapter
        binding.topicsList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.topicsList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        notificationsViewModel.topicsMap.observe(viewLifecycleOwner, Observer { topicsMap ->
            adapter.setTopics(topicsMap)
        })
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Notifications Enabled")
        } else {
            println("Notifications Disabled")

        }
    }

}