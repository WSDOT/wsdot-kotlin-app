package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.TollRowItemBinding
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateRow
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.TollRateRowDiffCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import java.util.*

class TollTableListAdapter (
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<TollRateRow, TollRowItemBinding>(
    appExecutors = appExecutors,
    diffCallback = TollRateRowDiffCallback()
) {

    override fun createBinding(parent: ViewGroup): TollRowItemBinding {

        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.toll_row_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: TollRowItemBinding, item: TollRateRow, position: Int) {

        binding.tollRateRow = item
        binding.tapView.background.clearColorFilter()

        // add as color filter so we can clear it back to the default color when needed
        if (item.header) {
            binding.tapView.background.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY)
        } else if (item.startTime != null && item.endTime != null){
            if (TimeUtils.isCurrentHour(item.startTime, item.endTime, Calendar.getInstance())){
                if ((item.weekday && !TimeUtils.weekendOrWAC468270071Holiday(Calendar.getInstance()))
                    || (!item.weekday && TimeUtils.weekendOrWAC468270071Holiday(Calendar.getInstance()))) {
                    binding.tapView.background.setColorFilter(Color.parseColor("#007b5f"), PorterDuff.Mode.SRC_IN)

                }
            }
        }
    }
}