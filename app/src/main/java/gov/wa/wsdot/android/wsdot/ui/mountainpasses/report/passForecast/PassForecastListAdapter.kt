package gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passForecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassForecast
import gov.wa.wsdot.android.wsdot.databinding.MountainPassForecastItemBinding
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [] class.
 */
class PassForecastListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<PassForecast, MountainPassForecastItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<PassForecast>() {
        override fun areItemsTheSame(oldItem: PassForecast, newItem: PassForecast): Boolean {
            return oldItem.forecastText == newItem.forecastText
                    && oldItem.day == newItem.day
        }

        override fun areContentsTheSame(oldItem: PassForecast, newItem: PassForecast): Boolean {
            return oldItem.forecastText == newItem.forecastText
                    && oldItem.day == newItem.day
        }
    }
) {

    var mObserver: RecyclerView.AdapterDataObserver? = null

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        mObserver = observer
        super.registerAdapterDataObserver(observer)

    }

    // This lets us add an observer that is dependant on the binding var
    // in this case we remove our auto scroll observer
    fun removeObserver() {
        mObserver?.let {
            unregisterAdapterDataObserver(it)
            mObserver = null
        }
    }

    override fun createBinding(parent: ViewGroup): MountainPassForecastItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.mountain_pass_forecast_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: MountainPassForecastItemBinding, item: PassForecast, position: Int) {
        binding.forecast = item
    }

}