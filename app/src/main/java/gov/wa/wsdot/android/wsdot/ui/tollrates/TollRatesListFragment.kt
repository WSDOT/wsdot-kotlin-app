package gov.wa.wsdot.android.wsdot.ui.tollrates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.di.Injectable

class TollRatesListFragment: Fragment(), Injectable {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val data = listOf(
            TollRateListItem(R.drawable.ic_list_sr16, "SR 16 Tacoma Narrows Bridge"),
            TollRateListItem(R.drawable.ic_list_sr99, "SR 99 Tunnel"),
            TollRateListItem(R.drawable.ic_list_sr167, "SR 167 Express Toll Lanes"),
            TollRateListItem(R.drawable.ic_list_sr509, "SR 509 Expressway"),
            TollRateListItem(R.drawable.ic_list_sr520, "SR 520 Bridge"),
            TollRateListItem(R.drawable.ic_list_i405, "I-405 Express Toll Lanes"),
        )

        val rootView: View = inflater.inflate(R.layout.toll_rate_list, container, false)
        val listView = rootView.findViewById<ListView?>(R.id.toll_rate_list_view)

        val adapter = TollRateListAdapter(activity, data)

        if (listView != null) {
            listView.adapter = adapter
        }

        listView?.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val route = parent.getItemAtPosition(position) as TollRateListItem

            when (route.text) {
                "SR 16 Tacoma Narrows Bridge" -> {
                    findNavController().navigate(R.id.action_TollRatesListFragment_to_SR16TollTableFragment)
                }
                "SR 99 Tunnel" -> {
                    findNavController().navigate(R.id.action_TollRatesListFragment_to_SR99TollTableFragment)
                }
                "SR 167 Express Toll Lanes" -> {
                    findNavController().navigate(R.id.action_TollRatesListFragment_to_SR167TollTableFragment)
                }
                "SR 509 Expressway" -> {
                    findNavController().navigate(R.id.action_TollRatesListFragment_to_SR509TollTableFragment)
                }
                "SR 520 Bridge" -> {
                    findNavController().navigate(R.id.action_TollRatesListFragment_to_SR520TollTableFragment)
                }
                "I-405 Express Toll Lanes" -> {
                    findNavController().navigate(R.id.action_TollRatesListFragment_to_I405TollTableFragment)
                }
            }
        }
        return rootView
    }
}