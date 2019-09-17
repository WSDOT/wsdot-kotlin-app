package gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateRow

class TollRateRowDiffCallback: DiffUtil.ItemCallback<TollRateRow>() {

    override fun areItemsTheSame(oldItem: TollRateRow, newItem: TollRateRow): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: TollRateRow, newItem: TollRateRow): Boolean {
        return false
    }
}