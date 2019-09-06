package gov.wa.wsdot.android.wsdot.ui.favorites.recyclerview

import androidx.recyclerview.widget.ListUpdateCallback

/**
 * Overrides the basic ListUpdateCallback class to make it
 * compatible with lists that have headers when not empty
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

    /**
     * Notifies the adapter which views to update on an insert.
     *
     * If the number of items being added {@code count} (+1 for header) is equal
     * to the whole section then we need to adjust the {@code position} (subtract 1)
     * and {@code count} (add 1) to account for the header.
     *
     * ex.
     * itemType = pass
     * insertCount = 1
     *
     * Adapter Pos | Section Pos | Type        |
     * ---------------------------------------------
     * 0           | 0           | header      |
     * 1           | 1           | ferry       |
     * 2           | 2           | ferry       |
     * 3           | 0           | header      |
     * 5           | 1           | pass        | If we are adding one pass, need to also include the above header
     * 6           | 0           | header      |
     * 7           | 1           | travel time |
     */
    override fun onInserted(position: Int, count: Int) {
        var adapterPosition = getItemPositionInAdapter(position, itemType)
        var insertCount = count

        if (insertCount + 1 == adapter.getNumItemsInSection(itemType)) {
            adapterPosition -= 1
            insertCount += 1
        }

        adapter.notifyItemRangeInserted(
            adapterPosition,
            insertCount
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
     *   section is removed, in which case the header view holder is
     *   also removed.
     */
    private fun getNumItemsRemoved(count: Int, itemType: Int): Int {
        return if (adapter.getNumItemsInSection(itemType) == 0) {
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
        val numItems = adapter.getNumItemsInSection(itemType)
        return if (numItems == 0) {
            pos
        } else {
            offsetPosition(pos)
        }
    }


}