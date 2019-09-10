package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing

object BorderCrossingBindingAdapters {

    @JvmStatic
    @BindingAdapter("bindCrossingWait")
    fun bindCrossingWait(textView: TextView, wait: Int) {

        textView.setTextColor(Color.parseColor("#FFFFFF"))

        if (wait != -1) {
            textView.text = String.format("%s min", wait)
            textView.setTextColor(Color.WHITE)
        } else {
            textView.text = "N/A"
            textView.setTextColor(Color.BLACK)
        }
    }

    @JvmStatic
    @BindingAdapter("bindCrossingColor")
    fun bindCrossingColor(cardView: CardView, wait: Int) {
        when {
            wait == -1 -> cardView.setCardBackgroundColor(Color.parseColor("#eeeeee"))
            wait < 30 -> cardView.setCardBackgroundColor(Color.parseColor("#007B5F"))
            wait < 60 -> cardView.setCardBackgroundColor(Color.parseColor("#bc5100"))
            else -> cardView.setCardBackgroundColor(Color.parseColor("#c62828"))
        }
    }

    @JvmStatic
    @BindingAdapter("bindCrossingIcon")
    fun bindCrossingIcon(imageView: ImageView, crossing: BorderCrossing) {

        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE

        val icon = getIconFromRoute(crossing.route, crossing.direction)

        icon?.let {
            imageView.setImageResource(it)
            imageView.visibility = View.VISIBLE
        }

    }

    private fun getIconFromRoute(route: Int, direction: String): Int? {
        when (route) {
            5 -> return when (direction) {
                "northbound" -> R.drawable.ic_list_i5
                "southbound" -> R.drawable.ic_list_bc99
                else -> null
            }
            9 -> return when (direction) {
                "northbound" -> R.drawable.ic_list_sr9
                "southbound" -> R.drawable.ic_list_bc11
                else -> null
            }
            543 -> return when (direction) {
                "northbound" -> R.drawable.ic_list_sr543
                "southbound" -> R.drawable.ic_list_bc15
                else -> null
            }
            539 -> return when (direction) {
                "northbound" -> R.drawable.ic_list_sr539
                "southbound" -> R.drawable.ic_list_bc13
                else -> null
            }
            else -> return null
        }
    }
}