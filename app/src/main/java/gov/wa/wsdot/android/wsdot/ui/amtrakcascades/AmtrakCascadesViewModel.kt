package gov.wa.wsdot.android.wsdot.ui.amtrakcascades

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.repository.AmtrakCascadesRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AmtrakCascadesViewModel @Inject constructor(amtrakCascadesRepository: AmtrakCascadesRepository) : ViewModel() {


    private val _departuresQuery: MutableLiveData<DeparturesQuery> = MutableLiveData()

    val departures: LiveData<Resource<List<AmtrakScheduleResponse>>> = Transformations
        .switchMap(_departuresQuery) { input ->
            input.ifExists { date, fromLocation, toLocation ->
                amtrakCascadesRepository.getDestinations(statusDate = date, fromLocation = fromLocation, toLocation = toLocation)
            }
        }

    val schedulePairs: MediatorLiveData<List<Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>>> = MediatorLiveData()

    // Used by spinner
    val originStations : List<Pair<String, String>> = arrayListOf(
        Pair("Vancouver, BC", "VAC"),
        Pair("Bellingham, WA", "BEL"),
        Pair("Mount Vernon, WA", "MVW"),
        Pair("Stanwood, WA", "STW"),
        Pair("Everett, WA", "EVR"),
        Pair("Edmonds, WA", "EDM"),
        Pair("Seattle, WA", "SEA"),
        Pair("Tukwila, WA", "TUK"),
        Pair("Tacoma, WA", "TAC"),
        Pair("Olympia, WA", "OLW"),
        Pair("Centralia, WA", "CTL"),
        Pair("Kelso/Longview, WA", "KEL"),
        Pair("Vancouver, WA", "VAN"),
        Pair("Portland, OR", "PDX"),
        Pair("Oregon City, OR", "ORC"),
        Pair("Salem, OR", "SLM"),
        Pair("Albany, OR", "ALY"),
        Pair("Eugene, OR", "EUG")

    )
    val destinationStations : List<Pair<String, String>> = arrayListOf(
        Pair("Any", "N/A"),
        Pair("Vancouver, BC", "VAC"),
        Pair("Bellingham, WA", "BEL"),
        Pair("Mount Vernon, WA", "MVW"),
        Pair("Stanwood, WA", "STW"),
        Pair("Everett, WA", "EVR"),
        Pair("Edmonds, WA", "EDM"),
        Pair("Seattle, WA", "SEA"),
        Pair("Tukwila, WA", "TUK"),
        Pair("Tacoma, WA", "TAC"),
        Pair("Olympia, WA", "OLW"),
        Pair("Centralia, WA", "CTL"),
        Pair("Kelso/Longview, WA", "KEL"),
        Pair("Vancouver, WA", "VAN"),
        Pair("Portland, OR", "PDX"),
        Pair("Oregon City, OR", "ORC"),
        Pair("Salem, OR", "SLM"),
        Pair("Albany, OR", "ALY"),
        Pair("Eugene, OR", "EUG")
    )

    // 2-way binding value for spinner
    private val _selectedOrigin = MediatorLiveData<Pair<String, String>>()
    val selectedOrigin: MutableLiveData<Pair<String, String>>
        get() = _selectedOrigin

    // 2-way binding value for spinner
    private val _selectedDestination = MediatorLiveData<Pair<String, String>>()
    val selectedDestination: MutableLiveData<Pair<String, String>>
        get() = _selectedDestination

    init {
        selectedOrigin.value = originStations[0]
        selectedDestination.value = destinationStations[0]

        // takes departure items from the API and converts them into departure and arrival Pairs.
        // !!! Special case to consider is when the destination is not selected. In this case the pairs will have the destination value nil.
        schedulePairs.addSource(departures) {

            var pairs = mutableListOf<Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>>()

            it.data?.let { scheduleItems ->

                var currentIndex = 0

                for ((index, item) in scheduleItems.withIndex()) {

                    if (index != currentIndex) { continue }

                    val nextItem = scheduleItems.getOrNull(index + 1)

                    if (nextItem != null && item.tripNumber == nextItem.tripNumber) {
                        currentIndex++
                        pairs.add(Pair(item, nextItem))
                    } else {
                        pairs.add(Pair(item, null))
                    }

                    currentIndex++
                }
            }
            schedulePairs.value = pairs
        }
    }

    fun refresh() {
        val origin = _departuresQuery.value?.fromLocation
        val destination = _departuresQuery.value?.toLocation
        val date = _departuresQuery.value?.date

        if (origin != null && destination != null && date != null) {
            _departuresQuery.value = DeparturesQuery(date, origin, destination)
        }
    }

    fun setDeparturesQuery(origin: String?, destination: String?) {

        var fromLocation = selectedOrigin.value?.second
        var toLocation = selectedDestination.value?.second

        destination?.let { toLocation = destination }
        origin?.let { fromLocation = origin }

        val format = SimpleDateFormat("MM-dd-yyyy")

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val date = _departuresQuery.value?.date ?: format.format(c.time) // set to current date if null

        if (fromLocation != null && toLocation != null && date != null) {

            val update = DeparturesQuery(
                date = date,
                fromLocation = fromLocation!!,
                toLocation = toLocation!!
            )
            if (_departuresQuery.value == update) {
                return
            }
            _departuresQuery.value = update
        }
    }

    // 09-26-2019
    fun setDeparturesQuery(departureDate: Date) {

        val origin = selectedOrigin.value?.second
        val destination = selectedDestination.value?.second

        val format = SimpleDateFormat("MM-dd-yyyy")
        format.format(departureDate)

        if (origin != null && destination != null) {

            val update = DeparturesQuery(
                date = format.format(departureDate),
                fromLocation = origin,
                toLocation = destination
            )

            if (_departuresQuery.value == update) {
                return
            }

            _departuresQuery.value = update
        }
    }
/*

    /*
     *  Swaps the initial terminal query if the arriving terminal is closer to the user than the departing.
     */
    fun selectStationNearestTo(location: Location) {

        val stationLocations = DistanceUtils.getStationLocations()

        var nearestStation: String? = null
        var winner = Int.MAX_VALUE

        for (station in stationLocations) {

                terminalLocations[terminalCombo.departingTerminalId]?.let {

                val contender = DistanceUtils.getDistanceFromPoints(it.latitude, it.longitude, location.latitude, location.longitude)

                if (contender < winner) {
                    winner = contender
                    nearestTerminalCombo = terminalCombo
                }
            }
        }

        nearestTerminalCombo?.let {termCombo ->
            _selectedOrigin.value = termCombo
        }

    }

 */

    data class DeparturesQuery(val date: String, val fromLocation: String, val toLocation: String) {
        fun <T> ifExists(f: (String, String, String) -> LiveData<T>): LiveData<T> {
            return if (date == "" || fromLocation == "") {
                AbsentLiveData.create()
            } else {
                f(date, fromLocation, toLocation)
            }
        }
    }
}
