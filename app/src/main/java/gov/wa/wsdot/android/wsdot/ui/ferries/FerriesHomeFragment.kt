package gov.wa.wsdot.android.wsdot.ui.ferries

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.di.Injectable
import java.util.logging.Logger
import javax.inject.Inject

class FerriesHomeFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var ferriesViewModel: FerriesViewModel


    private fun initRoutesList(viewModel: FerriesViewModel) {
        viewModel.routes.observe(viewLifecycleOwner, Observer { routes ->
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            Log.e("debug", routes.toString())
        })
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.ferries_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ferriesViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FerriesViewModel::class.java)

        initRoutesList(ferriesViewModel);

    }

}