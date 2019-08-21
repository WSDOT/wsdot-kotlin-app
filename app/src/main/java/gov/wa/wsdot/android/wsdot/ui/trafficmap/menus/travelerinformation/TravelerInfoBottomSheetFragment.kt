package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.eventItems.TravelerInfoMenuEventItem

class TravelerInfoBottomSheetFragment(private val travelerInfoMenuEventListener: TravelerInfoMenuEventListener) : BottomSheetDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)

        /*
        d.setOnShowListener {
            // prevents dragging behavior
            d.window?.let {
                (it.findViewById<View>(R.id.design_bottom_sheet).layoutParams as CoordinatorLayout.LayoutParams).behavior = null
            }
        }
*/

        return d
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.simple_bottom_sheet_list, container, false)

        val listItems = mutableListOf<TravelerInfoMenuEventItem>()
        listItems.add(
            TravelerInfoMenuEventItem("Travel Times", TravelerMenuItemType.TRAVEL_TIMES)
        )

        listItems.add(
            TravelerInfoMenuEventItem("Social Media Traffic Updates", TravelerMenuItemType.SOCIAL_MEDIA)
        )

        listItems.add(
            TravelerInfoMenuEventItem("Express Lanes", TravelerMenuItemType.EXPRESS_LANES)
        )

        listItems.add(
            TravelerInfoMenuEventItem("News Releases", TravelerMenuItemType.NEWS_ITEMS)
        )

        listItems.add(
            TravelerInfoMenuEventItem("Commercial Vehicle Restrictions", TravelerMenuItemType.COMMERCIAL_VEHICLE_RESTRICTIONS)
        )

        /*
        listItems.add(
            TravelerInfoMenuEventItem("Travel Charts", TravelerMenuItemType.TRAVEL_CHARTS)
        )
        */

        val listHeader = view.findViewById<TextView>(R.id.list_header)
        listHeader.text = "Traveler Information"

        val listView = view.findViewById<ListView>(R.id.bottom_sheet_list)

        context?.let {
            val adapter = TravelerInfoBottomSheetAdapter(
                it,
                this,
                listItems,
                travelerInfoMenuEventListener
            )
            listView.adapter = adapter
        }
        return view
    }
}
