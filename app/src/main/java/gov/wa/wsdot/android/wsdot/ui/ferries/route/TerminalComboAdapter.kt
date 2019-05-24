package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo

class TerminalComboAdapter(
    context: Context,
    textViewResourceId: Int,
    private val values: List<TerminalCombo>
) : ArrayAdapter<TerminalCombo>(context, textViewResourceId, values) {

    override fun getCount() = values.size
    override fun getItem(position: Int) = values[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        val text = values[position].departingTerminalName + " to " + values[position].arrivingTerminalName
        label.text = text
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        val text = values[position].departingTerminalName + " to " + values[position].arrivingTerminalName
        label.text = text
        return label
    }
}