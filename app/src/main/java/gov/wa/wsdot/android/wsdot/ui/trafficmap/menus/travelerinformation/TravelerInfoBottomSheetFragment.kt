package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.travelerinformation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.eventItems.TravelerInfoMenuEventItem

class TravelerInfoBottomSheetFragment(private val travelerInfoMenuEventListener: TravelerInfoMenuEventListener) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)
        d.setOnShowListener {
            // prevents dragging behavior
            d.window?.let {
                (it.findViewById<View>(R.id.design_bottom_sheet).layoutParams as CoordinatorLayout.LayoutParams).behavior = null
            }
        }
        return d
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.simple_bottom_sheet_list, container, false)

        val listItems = mutableListOf<TravelerInfoMenuEventItem>()
        listItems.add(
            TravelerInfoMenuEventItem("Travel Times", TravelerInfoMenuEventListener.TravelerMenuItemType.TRAVEL_TIMES)
        )
        listItems.add(
            TravelerInfoMenuEventItem("News Items", TravelerInfoMenuEventListener.TravelerMenuItemType.NEWS_ITEMS)
        )

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
