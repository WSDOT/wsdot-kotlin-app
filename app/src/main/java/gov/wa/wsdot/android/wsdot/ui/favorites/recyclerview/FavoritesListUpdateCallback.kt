package gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview

import androidx.recyclerview.widget.ListUpdateCallback

/**
 * Overrides the basic ListUpdateCallback class to make it
 * compatible with lists that have headers when not empty
 *
 *
 */
class FavoritesListUpdateCallback(
    private val adapter: FavoritesListAdapter,
    private val itemType: Int,
    private val offset: Int
) : ListUpdateCallback {

    private fun offsetPosition(originalPosition: Int): Int {
        return originalPosition + offset
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(
            getItemPositionInAdapter(position, itemType),
            getNumItemsRemoved(count, itemType)
        )
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(
            getItemPositionInAdapter(position, itemType),
            getNumItemsRemoved(count, itemType)
        )
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(
            offsetPosition(fromPosition),
            offsetPosition(toPosition))
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(
            getItemPositionInAdapter(position, itemType),
            count,
            payload
        )
    }

    /**
     *   Calculates the number of items removed on a delete swipe.
     *   Usually always one, except when the last item in a favorites
     *   section is removed, in which case the header viewholder is
     *   also removed.
     */
    private fun getNumItemsRemoved(count: Int, itemType: Int): Int {
        return if (adapter.getNumItemsFor(itemType) == 0) {
            count + 1
        } else {
            count
        }
    }

    /**
     *  Calculates the position of an item in the entire favorites list
     *  Needs to account for a header item
     */
    private fun getItemPositionInAdapter(position: Int, itemType: Int): Int {

        val pos = adapter.getPositionInAdapterForItem(position, itemType)
        val numItems = adapter.getNumItemsFor(itemType)

        return if (numItems == 0) {
            pos
        } else {
            offsetPosition(pos)
        }
    }

}