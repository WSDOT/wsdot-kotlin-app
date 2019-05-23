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

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.*
import androidx.fragment.app.Fragment
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.ui.ferries.route.TerminalComboAdapter
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {

    @BindingAdapter(value = ["terminalCombos", "selectedTerminalCombo", "selectedTerminalComboAttrChanged"], requireAll = false)
    fun setTerminalCombos(spinner: Spinner, terminal: Resource<List<TerminalCombo>>, selectedTerminal: TerminalCombo, listener: InverseBindingListener) {
        if (terminal.data == null) return
        spinner.adapter = TerminalComboAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, terminal.data)
        setCurrentSelection(spinner, selectedTerminal)
        setSpinnerListener(spinner, listener)
    }

    @InverseBindingAdapter(attribute = "selectedTerminalCombo", event = "selectedTerminalComboAttrChanged")
    fun getSelectedTerminalCombo(spinner: Spinner): TerminalCombo {
        return spinner.selectedItem as TerminalCombo
    }

    private fun setSpinnerListener(spinner: Spinner, listener: InverseBindingListener) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = listener.onChange()
            override fun onNothingSelected(adapterView: AdapterView<*>) = listener.onChange()
        }
    }

    private fun setCurrentSelection(spinner: Spinner, selectedItem: TerminalCombo?): Boolean {
        if (selectedItem == null) {
            return false
        }

        for (index in 0 until spinner.adapter.count) {
            val currentItem = spinner.getItemAtPosition(index) as TerminalCombo
            if ( (currentItem.departingTerminalId == selectedItem.departingTerminalId)
                && (currentItem.arrivingTerminalId == selectedItem.arrivingTerminalId)) {
                spinner.setSelection(index)
                return true
            }
        }
        return false
    }
}