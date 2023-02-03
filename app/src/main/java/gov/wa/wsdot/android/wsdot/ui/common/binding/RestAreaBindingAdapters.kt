package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.text.Html
import android.widget.TextView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.model.RestAreaItem
import java.util.ArrayList


object RestAreaBindingAdapters {

    @JvmStatic
    @BindingAdapter("bindRestAreaLocation")
    fun bindRestAreaLocation(textView: TextView, restArea: RestAreaItem?) {

        textView.text =
            Html.fromHtml("<b>" + restArea?.let { String.format(restArea.location) } + "</b>")

    }


    @JvmStatic
    @BindingAdapter("bindRestAreaDescription")
    fun bindRestAreaDescription(textView: TextView, restArea: RestAreaItem?) {

        textView.text =
            Html.fromHtml("<b>Location: </b>" + restArea?.let { String.format(restArea.description) })

    }

    @JvmStatic
    @BindingAdapter("bindRestAreaDirection")
    fun bindRestAreaDirection(textView: TextView, restArea: RestAreaItem?) {

        textView.text =
            Html.fromHtml("<b>Direction: </b>" + restArea?.let { String.format(restArea.direction) })

    }

    @JvmStatic
    @BindingAdapter("bindRestAreaMilePost")
    fun bindRestAreaMilePost(textView: TextView, restArea: RestAreaItem?) {

        textView.text =
            Html.fromHtml("<b>Mile Post: </b>" + restArea?.let { String.format(restArea.milepost.toString())})

    }

    @JvmStatic
    @BindingAdapter("bindRestAreaAmenities")
    fun bindRestAreaAmenities(textView: TextView, data: ArrayList<String>?) {

        val stringBuilder = StringBuilder()
        data?.let {

            for (string in it) {
                stringBuilder.append(string)
                stringBuilder.append(", ")
            }
        }

        textView.text =
            Html.fromHtml("<b>Amenities: </b>" + data?.let { stringBuilder.toString().dropLast(2)})

    }
}