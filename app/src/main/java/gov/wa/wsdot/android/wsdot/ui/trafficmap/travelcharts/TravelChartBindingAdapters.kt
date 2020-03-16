package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelChartsStatusResponse

object TravelChartBindingAdapters {

    @JvmStatic
    @BindingAdapter("bindTravelChartImage")
    fun bindTravelChartImage(imageView: ImageView, chart: TravelChartsStatusResponse.ChartRoute.TravelChart) {

        Glide.with(imageView)
            .load(chart.imageUrl)
            .placeholder(R.drawable.image_progress_animation)
            .error(R.drawable.camera_offline) // TODO: chart error image
            .fitCenter()
            .centerInside()
            .into(imageView)
    }

}