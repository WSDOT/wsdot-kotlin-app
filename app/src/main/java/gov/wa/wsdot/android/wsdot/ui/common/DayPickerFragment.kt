package gov.wa.wsdot.android.wsdot.ui.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import gov.wa.wsdot.android.wsdot.di.Injectable
import java.util.*
import javax.inject.Inject

class DayPickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var dayPickerViewModel: SharedDateViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        // Create a new instance of TimePickerDialog and return it
        val datePicker = DatePickerDialog(requireActivity(), this, year, month, day)

        datePicker.datePicker.minDate = c.timeInMillis
        c.add(Calendar.DAY_OF_YEAR, 7)
        datePicker.datePicker.maxDate = c.timeInMillis

        dayPickerViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(SharedDateViewModel::class.java)

        return datePicker
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.YEAR, year)

        dayPickerViewModel.setValue(c.time)

    }

}