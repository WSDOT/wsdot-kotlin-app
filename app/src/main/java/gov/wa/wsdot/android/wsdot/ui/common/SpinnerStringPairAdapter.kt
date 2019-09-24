package gov.wa.wsdot.android.wsdot.ui.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerStringPairAdapter(
    context: Context,
    textViewResourceId: Int,
    private val values: List<Pair<String, String>>
) : ArrayAdapter<Pair<String, String>>(context, textViewResourceId, values) {

    override fun getCount() = values.size
    override fun getItem(position: Int) = values[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        val text = values[position].first
        label.text = text
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        val text = values[position].first
        label.text = text
        return label
    }
}