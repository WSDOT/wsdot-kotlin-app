package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gov.wa.wsdot.android.wsdot.NavGraphDirections
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.eventItems.TravelerInfoMenuEventItem
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import kotlinx.parcelize.Parcelize


@Parcelize
class TravelerInfoBottomSheetFragment:
    BottomSheetDialogFragment(),
    TravelerInfoMenuEventListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.simple_bottom_sheet_list, container, false)

        val listItems = mutableListOf<TravelerInfoMenuEventItem>()

        println("screenWidth" + getScreenWidth())

        // Hides map legend on smaller screen sizes due to legibility issues.
        if (getScreenWidth() < 720) {
            val mapLegend = view.findViewById<ImageView>(R.id.map_legend)
            mapLegend.visibility = View.GONE
        }

        listItems.add(
            TravelerInfoMenuEventItem("Travel Times", TravelerMenuItemType.TRAVEL_TIMES)
        )

//        listItems.add(
//            TravelerInfoMenuEventItem("Twitter Traffic Updates", TravelerMenuItemType.SOCIAL_MEDIA)
//        )

        listItems.add(
            TravelerInfoMenuEventItem("Express Lanes", TravelerMenuItemType.EXPRESS_LANES)
        )

        listItems.add(
            TravelerInfoMenuEventItem("News Releases", TravelerMenuItemType.NEWS_ITEMS)
        )

        listItems.add(
            TravelerInfoMenuEventItem("Commercial Vehicle Restrictions",
                TravelerMenuItemType.COMMERCIAL_VEHICLE_RESTRICTIONS
            )
        )

        val listHeader = view.findViewById<TextView>(R.id.list_header)
        listHeader.text = "Traveler Information"

        val listView = view.findViewById<ListView>(R.id.bottom_sheet_list)

        context?.let {
            val adapter = TravelerInfoBottomSheetAdapter(
                it,
                this,
                listItems,
                this
            )
            listView.adapter = adapter
        }
        return view
    }

    // Traveler Info Menu Listener
    override fun travelerInfoMenuEvent(eventType: TravelerMenuItemType) {
        when (eventType) {
            TravelerMenuItemType.NEWS_ITEMS -> {
                val action =
                    TravelerInfoBottomSheetFragmentDirections.actionNavTrafficMapFragmentToNavNewsReleaseFragment()
                findNavController().navigate(action)
            }
            TravelerMenuItemType.EXPRESS_LANES -> {
                val action =
                    TravelerInfoBottomSheetFragmentDirections.actionNavTrafficMapFragmentToNavExpressLanesFragment()
                findNavController().navigate(action)
            }
            TravelerMenuItemType.SOCIAL_MEDIA -> {
                val action = NavGraphDirections.actionGlobalNavTwitterFragment()
                findNavController().navigate(action)
            }
            TravelerMenuItemType.TRAVEL_TIMES -> {
                val action =
                    TravelerInfoBottomSheetFragmentDirections.actionNavTrafficMapFragmentToNavTravelTimeListFragment()
                findNavController().navigate(action)
            }
            TravelerMenuItemType.COMMERCIAL_VEHICLE_RESTRICTIONS -> {
                val action = NavGraphDirections.actionGlobalNavWebViewFragment("https://wsdot.com/travel/real-time/truck-restrictions", "Restrictions")
                findNavController().navigate(action)
            }
        }
    }
}
