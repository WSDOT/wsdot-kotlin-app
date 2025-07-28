package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import javax.inject.Inject
import gov.wa.wsdot.android.wsdot.databinding.TerminalFragmentBinding
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import java.util.Timer
import java.util.TimerTask


class TerminalFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var terminalViewModel: TerminalViewModel

    lateinit var t: Timer

    private lateinit var toast: Toast
    private var paused = false

    val args: TerminalFragmentArgs by navArgs()


    @Inject
    lateinit var appExecutors: AppExecutors

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.terminal_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                terminalViewModel.refresh(args.terminalId)
                showToast("refreshing...")
                return false
            }
            else -> {}
        }
        return false
    }

    private fun showToast(message: String) {
        if (this::toast.isInitialized)
        {
            toast.cancel()
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,500)
        toast.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        terminalViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TerminalViewModel::class.java)
        terminalViewModel.setTerminalQuery(args.terminalId)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TerminalFragmentBinding>(
            inflater,
            R.layout.terminal_fragment,
            container,
            false
        )

        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.terminalViewModel = terminalViewModel

        startTerminalAlertTask()

        return dataBinding.root
    }

    override fun onPause() {
        super.onPause()
        paused = true
        t.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (paused) {
            terminalViewModel.refresh(args.terminalId)
            startTerminalAlertTask()
        }
        paused = false
    }


    private fun startTerminalAlertTask() {
        t = Timer()
        t.schedule(
            object : TimerTask() {
                override fun run() {
                    appExecutors.mainThread().execute {
                        terminalViewModel.refresh(args.terminalId)
                    }
                }
            },
            60000,
            120000
        )
    }
}