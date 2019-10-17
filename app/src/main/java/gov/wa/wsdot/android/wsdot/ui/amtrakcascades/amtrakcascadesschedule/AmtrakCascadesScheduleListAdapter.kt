package gov.wa.wsdot.android.wsdot.ui.amtrakcascades.amtrakcascadesschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.databinding.AmtrakCascadesItemBinding
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

class AmtrakCascadesScheduleListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>, AmtrakCascadesItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>>() {
        override fun areItemsTheSame(
            oldItem: Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>,
            newItem: Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>
        ): Boolean {
            val firstSame = oldItem.first.tripNumber == newItem.first.tripNumber
                    && oldItem.first.sortOrder == newItem.first.sortOrder

            var secondSame = true

            if (oldItem.second != null && newItem.second != null) {
                secondSame = oldItem.second!!.tripNumber == newItem.second!!.tripNumber
                        && oldItem.second!!.sortOrder == newItem.second!!.sortOrder

            } else if (oldItem.second != newItem.second) {
                secondSame = false
            }

            return firstSame && secondSame

        }

        override fun areContentsTheSame(
            oldItem: Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>,
            newItem: Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>
        ): Boolean {
            val firstSame = oldItem.first.arrivalComment == newItem.first.arrivalComment
                    && oldItem.first.departureComment == newItem.first.departureComment
                    && oldItem.first.arrivalTime == newItem.first.arrivalTime
                    && oldItem.first.departureTime== newItem.first.departureTime
                    && oldItem.first.scheduledDepartureTime == newItem.first.scheduledDepartureTime
                    && oldItem.first.updateTime == newItem.first.updateTime

            var secondSame = true

            if (oldItem.second != null && newItem.second != null) {
                secondSame = oldItem.second!!.arrivalComment == newItem.second!!.arrivalComment
                        && oldItem.second!!.departureComment == newItem.second!!.departureComment
                        && oldItem.second!!.arrivalTime == newItem.second!!.arrivalTime
                        && oldItem.second!!.departureTime== newItem.second!!.departureTime
                        && oldItem.second!!.scheduledDepartureTime == newItem.second!!.scheduledDepartureTime
                        && oldItem.second!!.updateTime == newItem.second!!.updateTime

            } else if (oldItem.second != newItem.second) {
                secondSame = false
            }

            return firstSame && secondSame
        }
    }
) {

    override fun createBinding(parent: ViewGroup): AmtrakCascadesItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.amtrak_cascades_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(
        binding: AmtrakCascadesItemBinding,
        item: Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>,
        position: Int
    ) {
        binding.departureItem = item.first
        binding.arrivalItem = item.second
    }

}