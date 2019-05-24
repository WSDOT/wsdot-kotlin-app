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

// https://stackoverflow.com/a/47746579/6135860


import android.graphics.Color
import android.util.Log
import androidx.databinding.BindingAdapter
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.ui.ferries.route.TerminalComboAdapter
import gov.wa.wsdot.android.wsdot.util.network.Resource
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    // Ferries Binding Adapters
    @JvmStatic
    @BindingAdapter(value = ["terminalCombos", "selectedTerminalCombo", "selectedTerminalComboAttrChanged"], requireAll = false)
    fun setTerminalCombos(spinner: Spinner, terminal: Resource<List<TerminalCombo>>, selectedTerminal: TerminalCombo, listener: InverseBindingListener) {
        if (terminal.data == null) return
        spinner.adapter = TerminalComboAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, terminal.data)

        setCurrentSelection(spinner, selectedTerminal)
        setSpinnerListener(spinner, listener)


    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedTerminalCombo", event = "selectedTerminalComboAttrChanged")
    fun getSelectedTerminalCombo(spinner: Spinner): TerminalCombo {
        Log.e("debug", "setting terminal combo in VM")
        return spinner.selectedItem as TerminalCombo
    }

    // Ferries spinner helpers

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

    private fun setCurrentSelection(spinner: Spinner, selectedItem: TerminalCombo?) {
        if (selectedItem == null) {
            return
        }
        for (index in 0 until spinner.adapter.count) {

            val currentItem = spinner.getItemAtPosition(index) as TerminalCombo

            if ( (currentItem.departingTerminalId == selectedItem.departingTerminalId)
                && (currentItem.arrivingTerminalId == selectedItem.arrivingTerminalId)) {

                //spinner.tag = index
                spinner.setSelection(index)

            }
        }
    }

    // General adapters

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
