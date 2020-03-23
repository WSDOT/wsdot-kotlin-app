package gov.wa.wsdot.android.wsdot.ui.amtrakcascades

import android.location.Location
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.repository.AmtrakCascadesRepository
import gov.wa.wsdot.android.wsdot.ui.amtrakcascades.helpers.AmtrakUtils
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.model.common.Status
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel to manage schedule data for Amtrak Cascades
 * Uses the AmtrakCascadesRepository to load in data from the WSDOT API.
 */
class AmtrakCascadesViewModel @Inject constructor(amtrakCascadesRepository: AmtrakCascadesRepository) : ViewModel() {

    private var didFindNearestStation = false

    private val _departuresQuery: MutableLiveData<DeparturesQuery> = MutableLiveData()

    // repo data with departure information for route queried byu the _departuresQuery Class
    private val departures: LiveData<Resource<List<AmtrakScheduleResponse>>> = Transformations
        .switchMap(_departuresQuery) { input ->
            input.ifExists { date, fromLocation, toLocation ->
                amtrakCascadesRepository.getDestinations(statusDate = date, fromLocation = fromLocation, toLocation = toLocation)
            }
        }

    // holds pairs of schedules, the first item is for the departure from an origin, the
    // second holds the arrival time at a destination.
    val schedulePairs: MediatorLiveData<Resource<List<Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>>>> = MediatorLiveData()

    // Lists of  available stations
    // used by origin spinner
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
    // used by destination spinner
    val destinationStations : List<Pair<String, String>> = arrayListOf(
        Pair("All", "N/A"),
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

            val pairs = mutableListOf<Pair<AmtrakScheduleResponse, AmtrakScheduleResponse?>>()

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

            when(it.status) {
                Status.LOADING -> schedulePairs.value = Resource.loading(pairs)
                Status.ERROR -> schedulePairs.value = Resource.error(it.message?: "error", pairs)
                Status.SUCCESS -> schedulePairs.value = Resource.success(pairs)
            }

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

        var mOrigin = selectedOrigin.value?.second
        var mDestination = selectedDestination.value?.second

        origin?.let { mOrigin = origin }
        destination?.let { mDestination = destination }

        val format = SimpleDateFormat("MM-dd-yyyy")

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val date = _departuresQuery.value?.date ?: format.format(c.time) // set to current date if null

        if (mOrigin != null && mDestination != null && date != null) {

            if (mOrigin == mDestination) {
                mDestination = "N/A"
            }

            val update = DeparturesQuery(
                date = date,
                fromLocation = mOrigin!!,
                toLocation = mDestination!!
            )
            if (_departuresQuery.value == update) {
                return
            }
            _departuresQuery.value = update
        }
    }

    // 09-26-2019
    fun setDeparturesQuery(departureDate: Date) {

        var origin = selectedOrigin.value?.second
        var destination = selectedDestination.value?.second

        val format = SimpleDateFormat("MM-dd-yyyy")
        format.format(departureDate)

        if (origin != null && destination != null) {

            if (origin == destination) {
                destination = "N/A"
            }

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

    fun selectStationNearestTo(location: Location) {

        // Only use GPS to find nearest station once per view model lifecycle
        if (didFindNearestStation) { return }
        didFindNearestStation = true

        val stationLocations = AmtrakUtils.getStationLocations()

        var nearestStation: Pair<String, String>? = null
        var winner = Int.MAX_VALUE

        for (station in originStations) {

            stationLocations[station.second]?.let {

                val contender = DistanceUtils.getDistanceFromPoints(it.latitude, it.longitude, location.latitude, location.longitude)

                if (contender < winner) {
                    winner = contender
                    nearestStation = station
                }
            }
        }

        nearestStation?.let {station ->
            _selectedOrigin.value = station
        }

    }

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
