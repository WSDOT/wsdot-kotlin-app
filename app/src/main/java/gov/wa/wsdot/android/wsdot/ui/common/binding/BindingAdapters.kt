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

import androidx.databinding.BindingAdapter
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import gov.wa.wsdot.android.wsdot.util.network.Resource
import android.widget.TextView
import android.text.Html
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import android.view.LayoutInflater
import android.widget.LinearLayout
import java.lang.StringBuilder


/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

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
        button.setImageResource(if (favorite) R.drawable.ic_menu_favorite_pink else R.drawable.ic_menu_favorite_gray)
    }

    @JvmStatic
    @BindingAdapter("bindCameraImage")
    fun bindCameraImage(imageView: ImageView, camera: Resource<Camera>) {
        if (camera.data != null) {
            Picasso.get()
                .load(camera.data.url)
                .placeholder(R.drawable.image_progress_animation)
                .error(R.drawable.camera_offline)
                .fit()
                .centerInside()
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("bindCameraImage")
    fun bindCameraImage(imageView: ImageView, camera: Camera) {
        Picasso.get()
            .load(camera.url)
            .placeholder(R.drawable.image_progress_animation)
            .error(R.drawable.camera_offline)
            .fit()
            .centerInside()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("bindCameraMilepost")
    fun bindCameraMilepost(textView: TextView, camera: Resource<Camera>) {
        if (camera.data != null) {
            textView.text = String.format("Near milepost %d", camera.data.milepost)
        }
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
        text?.let {
            textView.text = stripHtml(it).trimEnd()
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
    private fun getDateTimeStamp(date: Date): String {
        val displayDateFormat = SimpleDateFormat("MMMM d, YYYY h:mm a", Locale.ENGLISH)
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

        try {
            val relativeDate = Date()
            var delta = ((relativeDate.time - date.time) / 1000).toInt() // convert to seconds
            return if (delta < 60) {
                "Just now"
            } else if (delta < 120) {
                "1 minute ago"
            } else if (delta < 60 * 60) {
                Integer.toString(delta / 60) + " minutes ago"
            } else if (delta < 120 * 60) {
                "1 hour ago"
            } else if (delta < 24 * 60 * 60) {
                Integer.toString(delta / 3600) + " hours ago"
            } else {
                displayDateFormat.format(date)
            }
        } catch (e: Exception) {
            return "Unavailable"
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
