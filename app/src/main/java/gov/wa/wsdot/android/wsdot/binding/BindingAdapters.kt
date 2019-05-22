package gov.wa.wsdot.android.wsdot.binding

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

// https://stackoverflow.com/a/47746579/6135860

import androidx.databinding.BindingAdapter
import android.view.View
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*
import android.widget.TextView


/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("bindDate")
    fun bindDate(editText: EditText, date: Date) {
        editText.setText(getDateString(date))
    }

    @JvmStatic
    @BindingAdapter("bindRelativeDate")
    fun bindRelativeDate(textView: TextView, date: Date) {
        textView.text = getRelative(date)
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

}