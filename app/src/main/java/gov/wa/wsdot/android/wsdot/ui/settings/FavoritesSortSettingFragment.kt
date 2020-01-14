package gov.wa.wsdot.android.wsdot.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import dagger.android.support.DaggerFragment
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FavoritesSortSettingFragmentBinding
import gov.wa.wsdot.android.wsdot.di.Injectable
import gov.wa.wsdot.android.wsdot.ui.MainActivity
import gov.wa.wsdot.android.wsdot.ui.common.binding.FragmentDataBindingComponent
import gov.wa.wsdot.android.wsdot.ui.favorites.FavoritesFragment
import gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview.FavoritesListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import kotlinx.android.synthetic.main.amtrak_cascades_item.*
import javax.inject.Inject

class FavoritesSortSettingFragment : DaggerFragment(), Injectable, OnItemDragListener<String> {

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var adapter: FavoritesSortListAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setScreenName(this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding = DataBindingUtil.inflate<FavoritesSortSettingFragmentBinding>(
            inflater,
            R.layout.favorites_sort_setting_fragment,
            container,
            false
        )

        val viewTypes = FavoritesFragment.getOrderedViewTypes(context, resources)
        val headers = FavoritesListAdapter.headers

        val dataSet = mutableListOf<String>()

        for (viewType in viewTypes) {
            dataSet.add(headers[viewType]?:"error")
        }

        adapter = FavoritesSortListAdapter(dataSet)
        val list = dataBinding.list
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        list.dragListener = this

        list.orientation = DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
        list.orientation?.removeSwipeDirectionFlag(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.LEFT)
        list.orientation?.removeSwipeDirectionFlag(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.RIGHT)

        return dataBinding.root
    }

    override fun onItemDragged(previousPosition: Int, newPosition: Int, item: String) {}

    override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: String) {
        // Handle action of item dropped
        val headers = FavoritesListAdapter.headers
        FavoritesFragment.setOrderedViewTypes(context, resources, mutableListOf(
            headers.keys.first { headers[it] == adapter.dataSet[0]},
            headers.keys.first { headers[it] == adapter.dataSet[1]},
            headers.keys.first { headers[it] == adapter.dataSet[2]},
            headers.keys.first { headers[it] == adapter.dataSet[3]},
            headers.keys.first { headers[it] == adapter.dataSet[4]},
            headers.keys.first { headers[it] == adapter.dataSet[5]},
            headers.keys.first { headers[it] == adapter.dataSet[6]}
        ).toIntArray())

    }

}