package gov.wa.wsdot.android.wsdot.ui.common

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import java.util.*

class DayPickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    lateinit var dayPickerViewModel: SharedDateViewModel

    private val args: DayPickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Get view model from activity's lifecycle
        // Fragments can request the same view model to observe changes to data
        dayPickerViewModel = activity?.run {
            ViewModelProvider(this).get(SharedDateViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // Use the first date of sailings as the default values for the picker
        val c = Calendar.getInstance()
        c.timeInMillis = args.startTime

        val selectedDate = dayPickerViewModel.value.value
        if (selectedDate != null) {
            c.time = selectedDate
        }

        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        // Create a new instance of TimePickerDialog and return it
        val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day)
        datePickerDialog.datePicker.minDate = args.startTime
        datePickerDialog.datePicker.maxDate = args.endTime
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "",datePickerDialog) // hide cancel button
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "INFO"
        ) { _, _ ->
            // Ferry Schedule Calendar Message
            val ferryMessage =
                SpannableString("Future ferry schedules are provided for planning purposes and can change daily. Please monitor ferry alerts to stay notified of changes to your route. For additional trip planning information visit the <a href=https://wsdot.wa.gov/travel/washington-state-ferries>Washington State Ferries website</a>.")
            Linkify.addLinks(ferryMessage, Linkify.ALL)
            val alert: AlertDialog = AlertDialog.Builder(context)
                .setTitle("Ferry Schedule Calendar")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    datePickerDialog.show()
                }
                .setMessage(Html.fromHtml(ferryMessage.toString()))
                .create()
            alert.show()
            datePickerDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(View.OnClickListener {
                alert.show()
                datePickerDialog.dismiss()
            })
            (alert.findViewById<View>(android.R.id.message) as TextView?)!!.movementMethod =
                LinkMovementMethod.getInstance()
        }

        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.YEAR, year)
        dayPickerViewModel.setValue(c.time)

    }

}