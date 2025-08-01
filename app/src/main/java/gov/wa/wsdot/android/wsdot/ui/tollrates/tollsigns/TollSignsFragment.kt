package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TollSignFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.autoCleared
import gov.wa.wsdot.android.wsdot.model.common.Status
import javax.inject.Inject
import android.net.Uri
import android.content.Intent
import android.text.style.UnderlineSpan
import android.text.SpannableString
import android.view.Gravity
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import java.util.*

abstract class TollSignsFragment : DaggerFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var tollSignsViewModel: TollSignsViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<TollSignsListAdapter>()

    var binding by autoCleared<TollSignFragmentBinding>()

    lateinit var t: Timer

    private lateinit var toast: Toast
    private var isFavorite: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        tollSignsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TollSignsViewModel::class.java)
        tollSignsViewModel.setRoute(getRoute(), "N")

        initTravelTimeIds(tollSignsViewModel)

        // create the data binding
        val dataBinding = DataBindingUtil.inflate<TollSignFragmentBinding>(
            inflater,
            R.layout.toll_sign_fragment,
            container,
            false
        )

        binding = dataBinding

        val content = SpannableString(getInfoLinkText())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.infoButton.text = content

        binding.infoButton.setTextColor(resources.getColor(R.color.wsdotGreen))

        binding.infoButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(getInfoLinkURL())
            startActivity(intent)
        }

        binding.viewModel = tollSignsViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        val adapter =
            TollSignsListAdapter(
                dataBindingComponent,
                appExecutors,
                { sign ->
                    tollSignsViewModel.updateFavorite(sign.id, !sign.favorite)

                    isFavorite = sign.favorite

                    if (this::toast.isInitialized)
                    {
                        toast.cancel()
                    }

                    if (!isFavorite) {
                        toast = Toast.makeText(context, getString(R.string.favorite_added_message), Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,500)
                        toast.show()
                    }
                    else {
                        toast = Toast.makeText(context, getString(R.string.favorite_removed_message), Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,500)
                        toast.show()
                    }

                },
                { sign, index ->
                    if (sign.trips.size > index) {
                        navigateToMap(
                            LatLng(sign.startLatitude, sign.startLongitude),
                            LatLng(sign.trips[index].endLatitude, sign.trips[index].endLongitude))
                    }
                })

        this.adapter = adapter

        val itemDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDivider.setDrawable(resources.getDrawable(R.drawable.item_divider, null))
        binding.tollSignList.addItemDecoration(itemDivider)

        binding.tollSignList.adapter = adapter

        // animations
        postponeEnterTransition()
        binding.tollSignList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }

        tollSignsViewModel.tollSigns.observe(viewLifecycleOwner, Observer { signResource ->
            signResource.data?.let { it ->
                adapter.submitList(it)
                if (it.isEmpty() && signResource.status != Status.LOADING) {
                    // binding.emptyListView.visibility = View.VISIBLE
                    binding.tollSignList.visibility = View.GONE
                } else {
                    // binding.emptyListView.visibility = View.GONE
                    binding.tollSignList.visibility = View.VISIBLE
                }
            }

            if (signResource.status == Status.ERROR) {
                Toast.makeText(context, getString(R.string.loading_error_message), Toast.LENGTH_SHORT).show()
            }

        })

        tollSignsViewModel.tollTravelTimes.observe(viewLifecycleOwner, Observer { travelTimeRes ->
            travelTimeRes.data?.let {
                if (it.isNotEmpty()) {
                    binding.travelTimes.text = String.format(
                        "%s: %d min or %d min via ETL",
                        it[0].title,
                        it[0].currentTime,
                        it[0].hovCurrentTime
                    )
                }
            }
        })

        startTollRatesTask()

        return dataBinding.root
    }

    override fun onPause() {
        super.onPause()
        t.cancel()
    }

    private fun startTollRatesTask() {
        t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    appExecutors.mainThread().execute {
                        tollSignsViewModel.refresh()
                    }
                }
            },
            30000,
            120000
        )
    }

    private fun navigateToMap(startLocation: LatLng, endLocation: LatLng){
        val action = NavGraphDirections.actionGlobalNavTollTripFragment(
            startLatitude = startLocation.latitude.toString(),
            startLongitude = startLocation.longitude.toString(),
            endLatitude = endLocation.latitude.toString(),
            endLongitude = endLocation.longitude.toString(),
            title = "Toll Trip"
        )
        findNavController().navigate(action)
    }

    abstract fun getRoute(): Int
    abstract fun getInfoLinkText(): String
    abstract fun getInfoLinkURL(): String
    abstract fun initTravelTimeIds(viewModel: TollSignsViewModel)
}