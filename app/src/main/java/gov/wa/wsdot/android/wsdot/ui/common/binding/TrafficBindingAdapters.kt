package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.squareup.picasso.Picasso
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.ui.socialmedia.TwitterAccountAdapter

/**
 * Data Binding adapters specific to the app.
 */
object TrafficBindingAdapters {

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindTravelTime")
    fun bindTravelTime(textView: TextView, travelTime: TravelTime) {

        if (travelTime.currentTime < travelTime.avgTime - 1) {
            textView.setTextColor(Color.parseColor("#FFFFFF"))
        } else if (travelTime.currentTime > travelTime.avgTime + 1) {
            textView.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            textView.setTextColor(Color.parseColor("#000000"))
        }

        if (travelTime.currentTime != 0) {
            textView.text = String.format("%s min", travelTime.currentTime)
        } else {
            textView.text = "N/A"
        }
    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeInfo")
    fun bindTravelTimeInfo(textView: TextView, travelTime: TravelTime) {
        if (travelTime.miles != 0f && travelTime.avgTime != 0) {
            textView.text = String.format("%.2f miles / %s min", travelTime.miles, travelTime.avgTime)
        } else {
            textView.text = "Not Available"
        }
    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeColor")
    fun bindTravelTimeColor(cardView: CardView, travelTime: TravelTime) {
        if (travelTime.currentTime < travelTime.avgTime - 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#007B5F"))
        } else if (travelTime.currentTime > travelTime.avgTime + 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#c62828"))
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#eeeeee"))
        }
    }

    @JvmStatic
    @BindingAdapter("bindTweetMedia")
    fun bindTweetMedia(imageView: ImageView, tweet: Tweet) {
        imageView.visibility = GONE
        tweet.mediaUrl?.let {

            imageView.visibility = VISIBLE
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.image_progress_animation)
                .fit()
                .centerCrop()
                .into(imageView)
        }
    }
    


    // Two-way data binding for Twitter
    @JvmStatic
    @BindingAdapter(value = ["twitterAccount", "selectedTwitterAccount", "selectedTwitterAccountAttrChanged"], requireAll = false)
    fun setTwitterAccount(spinner: Spinner, accountNames: List<Pair<String, String>>, selectedAccount: Pair<String, String>?, listener: InverseBindingListener) {
        if (selectedAccount == null ) { return }
        spinner.adapter = TwitterAccountAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, accountNames)
        setCurrentSelection(spinner, selectedAccount)
        setSpinnerListener(spinner, listener)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedTwitterAccount", event = "selectedTwitterAccountAttrChanged")
    fun getSelectedTwitterAccount(spinner: Spinner): Pair<String, String> {
        return spinner.selectedItem as Pair<String, String>
    }

    // spinner helpers
    private fun setSpinnerListener(spinner: Spinner, listener: InverseBindingListener) {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            var lastPosition = spinner.selectedItemPosition

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // do nothing if same item is selected, this prevents infinite loops caused
                // by the 2-way binding.
                // https://medium.com/androiddevelopers/android-data-binding-2-way-your-way-ccac20f6313
                if (lastPosition != position) {
                    lastPosition = position
                    listener.onChange()
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) = listener.onChange()
        }
    }

    private fun setCurrentSelection(spinner: Spinner, selectedItem: Pair<String, String>) {
        for (index in 0 until spinner.adapter.count) {
            val currentItem = spinner.getItemAtPosition(index) as Pair<String, String>
            if (currentItem.first == selectedItem.first) {
                spinner.setSelection(index)
                return
            }
        }
        spinner.setSelection(0)
    }

}
