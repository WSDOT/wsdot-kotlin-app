package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.eventItems.TravelerInfoMenuEventItem

class TravelerInfoBottomSheetAdapter(
    context: Context,
    private val hostFragment: TravelerInfoBottomSheetFragment,
    private val dataSource: List<TravelerInfoMenuEventItem>,
    private val travelerInfoMenuEventListener: TravelerInfoMenuEventListener
): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    @SuppressLint("ViewHolder") // used for simple menu lists, no need for ViewHolder optimization
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.my_simple_list_item, parent, false)

        rowView.findViewById<TextView>(R.id.text).text = dataSource[position].name
        rowView.setOnClickListener {
            hostFragment.dismiss()
            travelerInfoMenuEventListener.travelerInfoMenuEvent(dataSource[position].type)
        }

        return rowView

    }
}