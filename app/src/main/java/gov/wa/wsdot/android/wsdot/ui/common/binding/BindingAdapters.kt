package gov.wa.wsdot.android.wsdot.ui.common.binding

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.ui.common.SpinnerStringPairAdapter
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    // Two-way data Binding functions for a spinner to display Pair<String, String> Objects.
    // The first name in the pair will display.
    // The second name can be used as key for a value.
    @JvmStatic
    @BindingAdapter(value = ["spinnerPairs", "selectedPair", "selectedPairAttrChanged"], requireAll = false)
    fun setStringPairs(spinner: Spinner, pairs: List<Pair<String, String>>, selectedPair: Pair<String, String>?, listener: InverseBindingListener) {
        if (selectedPair == null ) { return }
        spinner.adapter = SpinnerStringPairAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, pairs)
        setCurrentSelection(spinner, selectedPair)
        setSpinnerListener(spinner, listener)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedPair", event = "selectedPairAttrChanged")
    fun getSelectedStringPair(spinner: Spinner): Pair<String, String> {
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


    // General adapters
    @JvmStatic
    @BindingAdapter("bindListToTextView")
    fun bindListToTextView(textView: TextView, data: ArrayList<String>?) {

        textView.setSingleLine(false)

        val stringBuilder = StringBuilder()
        data?.let {

            textView.setLines(it.size)
            for (string in it) {
                stringBuilder.append("\u2022  ")
                stringBuilder.append(string)
                stringBuilder.append(" \n")
            }
        }
        textView.text = stringBuilder.toString()
    }

    @JvmStatic
    @BindingAdapter("setCameraIconForFab")
    fun setCameraIconForFab(fab: FloatingActionButton, showCamera: Boolean) {
        fab.setImageResource(if (showCamera) R.drawable.ic_camera_on else R.drawable.ic_camera_off)
    }

    @JvmStatic
    @BindingAdapter("setFavoriteIcon")
    fun bindFavoriteIcon(button: ImageButton, favorite: Boolean) {
        button.setImageResource(if (favorite) R.drawable.ic_menu_favorite_pink else R.drawable.ic_menu_favorite_outline)

        if (favorite) {
            button.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFF94C2"))
        } else {
            button.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF9e9e9e"))
        }

    }

    @JvmStatic
    @BindingAdapter("bindCameraImage")
    fun bindCameraImage(imageView: ImageView, camera: Resource<Camera>) {
        if (camera.data != null) {

            Glide.with(imageView)
                .load(camera.data.url + String.format("?%d", Date().time))
                .placeholder( if (android.os.Build.VERSION.SDK_INT > 22) { R.drawable.image_progress_animation } else { R.drawable.image_placeholder })
                .error(R.drawable.camera_offline)
                .centerInside()
                .into(imageView)

        }
    }

    @JvmStatic
    @BindingAdapter("bindCameraImage")
    fun bindCameraImage(imageView: ImageView, camera: Camera) {
        Glide.with(imageView)
            .load(camera.url + String.format("?%d", Date().time))
            .placeholder( if (android.os.Build.VERSION.SDK_INT > 22) { R.drawable.image_progress_animation } else { R.drawable.image_placeholder })
            .error(R.drawable.camera_offline)
            .centerInside()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("bindCameraDirection")
    fun bindCameraDirection(textView: TextView, camera: Resource<Camera>) {
        if (camera.data?.direction != null) {
            var cameraDirection = camera.data?.direction

            if (cameraDirection == "B") {
                cameraDirection = "This camera moves to point in more than one direction."
            }
            if (cameraDirection == "N") {
                cameraDirection = "North"
            }
            if (cameraDirection == "E") {
                cameraDirection = "East"
            }
            if (cameraDirection == "S") {
                cameraDirection = "South"
            }
            if (cameraDirection == "W") {
                cameraDirection = "West"
            }
            textView.text = Html.fromHtml("<b>Camera Direction: </b>" + String.format(cameraDirection))
            textView.visibility = View.VISIBLE

        } else {
            textView.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("bindCameraRefreshLabel")
    fun bindCameraRefreshLabel(textView: TextView, camera: Resource<Camera>) {
            textView.text = Html.fromHtml("<b>Refresh Rate: </b>Approximately every 5 minutes.")
    }

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("setMax")
    fun bindSetMax(progress: ProgressBar, max: Int?) {
        if (max != null) {
            progress.max = max
        }
    }

    @JvmStatic
    @BindingAdapter("setCurrent")
    fun bindSetCurrent(progress: ProgressBar, current: Int?) {
        if (current != null) {
            progress.progress = current
        }
    }

    @JvmStatic
    @BindingAdapter("setTextFromInt")
    fun bindSetCurrent(textView: TextView, int: Int?) {
        if (int != null) {
            textView.text = int.toString()
        }
    }

    @JvmStatic
    @BindingAdapter("bindStringArray")
    fun bindStringArray(textView: TextView, strings: List<String>) {
        var messageString = ""
        for (string in strings) {
            messageString = "$messageString$string "
        }
        textView.text = stripHtml(messageString)
    }

    @JvmStatic
    @BindingAdapter("bindHTML")
    fun bindHTML(textView: TextView, text: String?) {

        "Unavailable".also { textView.text = it }

        if (text != null) {
            if (text != "") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).trimEnd()
                    textView.movementMethod = LinkMovementMethod.getInstance()
                } else {
                    textView.text = stripHtml(text).trimEnd()
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindAlertHTML")
    fun bindAlertHTML(textView: TextView, text: String?) {

        if (text != null) {
            if (text != "") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).trimEnd()
                    textView.movementMethod = LinkMovementMethod.getInstance()
                } else {
                    textView.text = stripHtml(text).trimEnd()
                }
            }
            else {
                "".also { textView.text = it }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindRoadName")
    fun bindRoadName(textView: TextView, text: String?) {

        if (!text.toString().contains("null")) {
                textView.text = text
                textView.visibility = View.VISIBLE
        }
        else {
            textView.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("bindDate")
    fun bindDate(editText: EditText, date: Date) {
        editText.setText(getDateString(date))
    }

    @JvmStatic
    @BindingAdapter("bindDate")
    fun bindDate(textView: TextView, date: Date?) {
        date?.let { textView.text = getDateTimeStamp(it) }
    }

    @JvmStatic
    @BindingAdapter("bindShortDate")
    fun bindShortDate(editText: EditText, date: Date) {
        editText.setText(getShortDateString(date))
    }

    @JvmStatic
    @BindingAdapter("bindDateHour")
    fun bindDateHour(textView: TextView, date: Date?) {
        if (date != null) {
            textView.text = getHourString(date)
        }
    }

    @JvmStatic
    @BindingAdapter("bindRelativeDate")
    fun bindRelativeDate(textView: TextView, date: Date?) {
        if (date != null) {
            textView.text = getRelative(date)
        }
    }

    @JvmStatic
    @BindingAdapter("bindOpeningTimeDate")
    fun bindOpeningTimeDate(textView: TextView, date: Date?) {
        if (date != null) {
            textView.visibility = View.VISIBLE
            date.let { textView.text = "Opening Time: " + getDateTimeStamp(it) }
        }
        else {
            textView.visibility = View.GONE
        }
    }

    // Creates an updated timestamp from date object
    private fun getDateString(date: Date): String {
        val displayDateFormat = SimpleDateFormat("EEE, MMMM d", Locale.ENGLISH)
        return try {
            displayDateFormat.format(date)
        } catch (e: Exception) {
            "Unavailable"
        }
    }

    // Creates an updated timestamp from date object
    private fun getShortDateString(date: Date): String {
        val displayDateFormat = SimpleDateFormat("EEE, MMM d", Locale.ENGLISH)
        return try {
            displayDateFormat.format(date)
        } catch (e: Exception) {
            "Unavailable"
        }
    }

    // Creates an updated timestamp from date object
    private fun getDateTimeStamp(date: Date): String {
        val displayDateFormat = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH)
        return try {
            displayDateFormat.format(date)
        } catch (e: Exception) {
            "Unavailable"
        }
    }

    // Creates an updated timestamp from date object
    fun getHourString(date: Date): String {
        val displayDateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        return try {
            displayDateFormat.format(date)
        } catch (e: Exception) {
            "Unavailable"
        }
    }


    // Creates an updated timestamp from date object
    private fun getRelative(date: Date): String {

        val displayDateFormat = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH)

        return try {
            val relativeDate = Date()
            val delta = ((relativeDate.time - date.time) / 1000).toInt() // convert to seconds
            when {
                delta < 60 -> "Just now" // < 1 minute
                delta < 120 -> "1 minute ago" // < 2 minutes
                delta < 3600 -> (delta / 60).toString() + " minutes ago" // < 1 hour
                delta < 7200 -> "1 hour ago" // < 2 hours
                delta < 86400 -> (delta / 3600).toString() + " hours ago" // < 1 day
                delta < 172800 -> "1 day ago" // < 2 days
                delta < 604800 -> (delta / 86400).toString() + " days ago" // < 7 days
                delta < 1209600 -> "1 week ago" // < 14 days
                delta < 2629800 -> (delta / 604800).toString() + " weeks ago" // < 1 month
                delta < 5259600 -> "1 month ago" // < 2 months
                delta < 31557600 -> (delta / 2629800).toString() + " months ago" // < 1 year
                delta < 63115200 -> "1 year ago" // < 2 years
                delta < 157788000 -> (delta / 31557600).toString() + " years ago" // < 5 years
                else -> displayDateFormat.format(date)
            }
        } catch (e: Exception) {
            "Unavailable"
        }
    }

    private fun stripHtml(html: String): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(html).toString()
        }
    }

}
