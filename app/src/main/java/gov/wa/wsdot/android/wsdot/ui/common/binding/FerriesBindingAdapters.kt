package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.R
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithStatus
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalAlert
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.ui.ferries.route.TerminalComboAdapter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data Binding adapters specific to the app.
 */
object FerriesBindingAdapters {

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindVesselName")
    fun bindVesselName(textView: TextView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {
            textView.text = vessel.data.vesselName
            return
        }
        textView.text = ""
    }

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindTerminalName")
    fun bindTerminalName(webView: WebView, terminal: Resource<TerminalAlert>) {

        val start = "<html><head><style>*{font-size: 18px}</style></head><body>"
        val end = "</body></html>"

        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.getSettings(), true)
        }

        webView.setBackgroundColor(Color.TRANSPARENT)

        if (terminal.data != null) {
            webView.loadData(start + "<b>" + terminal.data.terminalName + " Terminal</b>" + end,"text/html; charset=utf-8",
                null)
        }
        else {
        webView.loadData("","text/html; charset=utf-8",null)
        }
    }


    @JvmStatic
    @BindingAdapter("bindTerminalAddress")
    fun bindTerminalAddress(webView: WebView, terminal: Resource<TerminalAlert>) {

        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.getSettings(), true)
        }

        webView.setBackgroundColor(Color.TRANSPARENT)

        if (terminal.data != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    webView.loadData(terminal.data.addressLineOne + "<br>" +  terminal.data.city + " " + terminal.data.state + " " + terminal.data.zipCode + "<br>","text/html; charset=utf-8",null)
                }
            return
        }
        else {
            webView.loadData("","text/html; charset=utf-8",null)
        }
    }

    @JvmStatic
    @BindingAdapter("bindTerminalBulletins")
    fun bindTerminalBulletins(webView: WebView, terminal: Resource<TerminalAlert>) {

        val alerts = ArrayList<String>()

        val start = "<html><head><style>*{prefers-color-scheme: dark}a{color: rgb(51, 149, 127)}.footer{opacity: 0.6;font-size: .8em}@media (prefers-color-scheme: dark) {hr{border-color: rgb(0, 0, 0)}}</style></head><body>"
        val end = "</body></html>"

        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.getSettings(), true)
        }

        webView.settings.defaultFontSize = 16
        webView.setBackgroundColor(Color.TRANSPARENT)

        if (terminal.data != null) {

            if (!terminal.data.bulletins.isEmpty()) {
                for (item in terminal.data.bulletins) {
                    alerts.add(
                        "<hr><br><b>" + item.bulletinTitle + "</b><br>" + item.bulletinText + "<br><span class=\"footer\">" + getDateString(
                            Date(item.bulletinLastUpdated.drop(6).dropLast(10).toLong() * 1000)
                        ) + "</span><br><br>"
                    )
                }
                webView.loadData(
                    start + alerts.joinToString(separator = "").replace(Regex("#"), "&num;") + "<hr>" + end,
                    "text/html; charset=utf-8",
                    null
                )
                return
            }
            else {
                val bulletin = "<hr><li><b>No Posted Bulletins</b><hr><br>"
                webView.loadData(start + bulletin + end, "text/html; charset=utf-8", null)
            }
        }
        else {
            webView.loadData("", "text/html; charset=utf-8", null)
        }
    }

    @JvmStatic
    @BindingAdapter("bindSailingTextColor")
    fun bindSailingTextColor(textView: TextView, sailingDate: Date?) {
        textView.setTextColor(Color.WHITE)
        sailingDate?.let {
            if (sailingDate.before(Date())) {
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindReservations")
    fun bindReservations(textView: TextView, sailing: FerrySailingWithStatus) {

        textView.text = ""

        if (sailing.showResSpaces) {
            sailing.reserveSpaces?.let {
                if (it == 1) {
                    textView.text = String.format("%d RESERVATION SPACE", it)
                } else {
                    textView.text = String.format("%d RESERVATION SPACES", it)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindVesselRoute")
    fun bindVesselRoute(textView: TextView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {
            vessel.data.arrivingTerminalName?.isEmpty()?.let {
                if ((!vessel.data.departingTerminalName.isEmpty()) && (!it)) {

                    textView.text = vessel.data.departingTerminalName + " to " + vessel.data.arrivingTerminalName
                    return
                }
            }
            textView.text = "Not Available"
        }
    }


    /**
     *  sets textView to display vessel name, as well as if the vessel is at
     *  the dock.
     */
    @JvmStatic
    @BindingAdapter("bindVesselStatus")
    fun bindVesselStatus(textView: TextView, sailing: FerrySailingWithStatus?) {
        if (sailing != null) {

            textView.visibility = VISIBLE

            val atDock = sailing.vesselAtDock ?: false

            val vesselName = when {
                sailing.vesselName != null -> {
                    sailing.vesselName.uppercase(Locale.ENGLISH) + " "
                }
                else -> { sailing.vesselName }
            }

            when {
                atDock -> {
                    textView.text = vesselName + "VESSEL AT DOCK"
                }
                vesselName != null -> {
                    textView.text = vesselName + "VESSEL"
                }
                else -> {
                    textView.visibility = GONE
                }
            }

        }
    }

    @JvmStatic
    @BindingAdapter("bindVesselSchDeparture")
    fun bindVesselSchDeparture(textView: TextView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {
            if (vessel.data.scheduledDeparture != null) {
                textView.text = BindingAdapters.getHourString(vessel.data.scheduledDeparture)
                return
            }
        }
        textView.text = "--:--"
    }

    @JvmStatic
    @BindingAdapter("bindVesselActualDeparture")
    fun bindVesselActualDeparture(textView: TextView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {
            if (vessel.data.leftDock != null) {
                textView.text = BindingAdapters.getHourString(vessel.data.leftDock)
                return
            }
        }
        textView.text = "--:--"
    }

    @JvmStatic
    @BindingAdapter("bindVesselEta")
    fun bindVesselEta(textView: TextView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {
            if (vessel.data.eta != null) {
                textView.text = BindingAdapters.getHourString(vessel.data.eta)
                return
            }
        }
        textView.text = "--:--"
    }

    @JvmStatic
    @BindingAdapter("bindVesselUpdated")
    fun bindVesselUpdated(textView: TextView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {
            textView.text = getDateString(vessel.data.serverCacheDate)
            return
        }
        textView.text = "Not Available"
    }
    @JvmStatic
    @BindingAdapter("bindVesselImage")
    fun bindVesselImage(imageView: ImageView, vessel: Resource<Vessel>) {
        if (vessel.data != null) {

            when (vessel.data.vesselName.lowercase()) {
                "cathlamet" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.cathlamet)
                "chelan" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.chelan)
                "chetzemoka" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.chetzemoka)
                "chimacum" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.chimacum)
                "issaquah" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.issaquah)
                "kaleetan" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.kaleetan)
                "kennewick" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.kennewick)
                "kitsap" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.kitsap)
                "kittitas" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.kittitas)
                "puyallup" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.puyallup)
                "salish" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.salish)
                "samish" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.samish)
                "sealth" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.sealth)
                "spokane" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.spokane)
                "suquamish" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.suquamish)
                "tacoma" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.tacoma)
                "tillikum" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.tillikum)
                "tokitae" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.tokitae)
                "walla walla" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.wallawalla)
                "wenatchee" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.wenatchee)
                "yakima" -> imageView.setImageResource(gov.wa.wsdot.android.wsdot.R.drawable.yakima)
                else -> imageView.visibility = GONE
            }
            return
        }
    }

    // Two-way binding adapters
    @JvmStatic
    @BindingAdapter(value = ["terminalCombos", "selectedTerminalCombo", "selectedTerminalComboAttrChanged"], requireAll = false)
    fun setTerminalCombos(spinner: Spinner, terminal: Resource<List<TerminalCombo>>, selectedTerminal: TerminalCombo?, listener: InverseBindingListener) {
        if (terminal.data == null) return
        spinner.adapter = TerminalComboAdapter(spinner.context, R.layout.simple_spinner_dropdown_item, terminal.data)

        setCurrentSelection(spinner, selectedTerminal)
        setSpinnerListener(spinner, listener)

    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedTerminalCombo", event = "selectedTerminalComboAttrChanged")
    fun getSelectedTerminalCombo(spinner: Spinner): TerminalCombo {
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
                spinner.setSelection(index)
                return
            }
        }

        spinner.setSelection(0)

    }

    // Creates an updated timestamp from date object
    private fun getDateString(date: Date): String {
        val displayDateFormat = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH)

        return try {
            val relativeDate = Date()
            val delta = ((relativeDate.time - date.time) / 1000).toInt() // convert to seconds
            when {
                delta < 3 -> "Just now" // < 1 minute
                delta < 60 -> (delta).toString() + " seconds ago"
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
            "Not Available"
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
