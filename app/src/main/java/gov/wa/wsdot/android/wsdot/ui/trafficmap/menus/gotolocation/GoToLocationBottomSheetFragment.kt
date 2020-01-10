package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.gotolocation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.model.MapLocationItem
import gov.wa.wsdot.android.wsdot.model.eventItems.GoToLocationMenuEventItem
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.trafficmap.MapLocationViewModel
import javax.inject.Inject

class GoToLocationBottomSheetFragment : BottomSheetDialogFragment(),
    GoToLocationMenuEventListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var mapLocationViewModel: MapLocationViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.simple_bottom_sheet_list, container, false)

        mapLocationViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(MapLocationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val listItems = mutableListOf<GoToLocationMenuEventItem>()
        listItems.add(
            GoToLocationMenuEventItem(
                "Bellingham",
                LatLng(48.756302, -122.46151),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Chehalis",
                LatLng(46.635529, -122.937698),
                11.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Everett",
                LatLng(47.967976, -122.197627),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Hood Canal",
                LatLng(47.85268, -122.628365),
                13.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Monroe",
                LatLng(47.859476, -121.97244),
                14.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Mt. Vernon",
                LatLng(48.420657, -122.334824),
                13.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Olympia",
                LatLng(47.021461, -122.899933),
                13.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Seattle",
                LatLng(47.5990, -122.3350),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Stanwood",
                LatLng(48.22959, -122.34581),
                13.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Sultan",
                LatLng(47.86034, -121.812286),
                13.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Spokane",
                LatLng(47.658566, -117.425995),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Tacoma",
                LatLng(47.206275, -122.46254),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Vancouver",
                LatLng(45.639968, -122.610512),
                11.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Wenatchee",
                LatLng(47.435867, -120.309563),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Snoqualmie Pass",
                LatLng(47.404481, -121.4232569),
                12.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Tri-Cities",
                LatLng(46.2503607, -119.2063781),
                11.0f
            )
        )
        listItems.add(
            GoToLocationMenuEventItem(
                "Yakima",
                LatLng(46.6063273, -120.4886952),
                11.0f
            )
        )

        val listHeader = view.findViewById<TextView>(R.id.list_header)
        listHeader.text = "Go To..."

        val listView = view.findViewById<ListView>(R.id.bottom_sheet_list)
        context?.let {
            val adapter = GoToLocationBottomSheetAdapter(
                it,
                this,
                listItems,
                this
            )
            listView.adapter = adapter
        }

        return view
    }

    // GoToLocationListener
    override fun goToLocation(goToLocationItem: GoToLocationMenuEventItem) {
        mapLocationViewModel.updateLocation(
            MapLocationItem(
                goToLocationItem.location,
                goToLocationItem.zoom
            )
        )
    }
}
