package gov.wa.wsdot.android.wsdot.ui.settings

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import gov.wa.wsdot.android.wsdot.R


class FavoritesSortListAdapter(dataSet: List<String> = emptyList()): DragDropSwipeAdapter<String, FavoritesSortListAdapter.ViewHolder>(dataSet) {

    class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.item_text)
        val dragIcon: ImageView = itemView.findViewById(R.id.drag_icon)
    }

    override fun getViewHolder(itemLayout: View) = FavoritesSortListAdapter.ViewHolder(itemLayout)

    override fun onBindViewHolder(item: String, viewHolder: FavoritesSortListAdapter.ViewHolder, position: Int) {
        // Here we update the contents of the view holder's views to reflect the item's data
        viewHolder.itemText.text = item
    }

    override fun getViewToTouchToStartDraggingItem(item: String, viewHolder: FavoritesSortListAdapter.ViewHolder, position: Int): View? {
        // We return the view holder's view on which the user has to touch to drag the item
        return viewHolder.dragIcon
    }
}
