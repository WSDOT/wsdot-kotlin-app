package gov.wa.wsdot.android.wsdot.ui.tollrates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import gov.wa.wsdot.android.wsdot.R

class TollRateListAdapter(context: FragmentActivity?, private val items: List<TollRateListItem>) :
    ArrayAdapter<TollRateListItem>(context!!, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.toll_rate_list_item, parent, false)
        }

        val currentItem = items[position]
        val imageView: ImageView = itemView!!.findViewById(R.id.tollImageView)
        val textView: TextView = itemView.findViewById(R.id.tollTextView)

        imageView.setImageResource(currentItem.imageId)
        textView.text = currentItem.text

        return itemView
    }
}