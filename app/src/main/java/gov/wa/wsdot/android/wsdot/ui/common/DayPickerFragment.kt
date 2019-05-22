package gov.wa.wsdot.android.wsdot.ui.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import java.util.*


class DayPickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    lateinit var dayPickerViewModel: SharedDateViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Get view model from activity's lifecycle
        // Fragments can request the same view model to observe changes to data
        dayPickerViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()

        val selectedDate = dayPickerViewModel.value.value
        if (selectedDate != null) {
            c.time = selectedDate
        }

        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        // Create a new instance of TimePickerDialog and return it
        val datePicker = DatePickerDialog(requireActivity(), this, year, month, day)

        val rangeCal = Calendar.getInstance()
        datePicker.datePicker.minDate = rangeCal.timeInMillis
        rangeCal.add(Calendar.DAY_OF_YEAR, 7)
        datePicker.datePicker.maxDate = rangeCal.timeInMillis

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