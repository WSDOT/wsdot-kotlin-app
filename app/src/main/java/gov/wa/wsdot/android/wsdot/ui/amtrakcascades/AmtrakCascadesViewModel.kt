package gov.wa.wsdot.android.wsdot.ui.amtrakcascades

import android.location.Location
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleRange
import gov.wa.wsdot.android.wsdot.repository.AmtrakCascadesRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AmtrakCascadesViewModel @Inject constructor(amtrakCascadesRepository: AmtrakCascadesRepository) : ViewModel() {

    private val repo = amtrakCascadesRepository

    private val _departuresQuery: MutableLiveData<DeparturesQuery> = MutableLiveData()

    val departures: LiveData<Resource<List<AmtrakScheduleResponse>>> = Transformations
        .switchMap(_departuresQuery) { input ->
            input.ifExists { date, fromLocation, toLocation ->
                amtrakCascadesRepository.getDestinations(date, fromLocation, toLocation)
            }
        }

    // Used by spinner
    val originStations : List<Pair<String, String>> = arrayListOf(
        Pair("Bellingham", "BEL"),
        Pair("Olympia", "OLW")
    )
    val destinationStations : List<Pair<String, String>> = arrayListOf(
        Pair("Any", "N/A"),
        Pair("Bellingham", "BEL"),
        Pair("Olympia", "OLW")
    )

    // 2-way binding value for spinner
    private val _selectedOrigin = MediatorLiveData<Pair<String, String>>()
    val selectedOrigin: MutableLiveData<Pair<String, String>>
        get() = _selectedOrigin

    // 2-way binding value for spinner
    private val _selectedDestination = MediatorLiveData<Pair<String, String>>()
    val selectedDestination: MutableLiveData<Pair<String, String>>
        get() = _selectedDestination

    // 2-way binding value for spinner
    private val _selectedDate = MediatorLiveData<String>()
    val selectedDate: MutableLiveData<String>
        get() = _selectedDate

    init {
        selectedOrigin.value = originStations[0]
        selectedDestination.value = destinationStations[0]
    }


    fun refresh() {
        val origin = _departuresQuery.value?.fromLocation
        val destination = _departuresQuery.value?.toLocation
        val date = _departuresQuery.value?.date

        if (origin != null && destination != null && date != null) {
            _departuresQuery.value = DeparturesQuery(date, origin, destination)
        }
    }

    // 09-26-2019
    fun setDeparturesQuery(departureDate: Date) {

        val origin = _departuresQuery.value?.fromLocation
        val destination = _departuresQuery.value?.toLocation

        val format = SimpleDateFormat("MM-dd-yyyy")
        format.format(departureDate)

        if (origin != null && destination != null) {

            val update = DeparturesQuery(
                origin,
                destination,
                format.format(departureDate)
            )

            if (_departuresQuery.value == update) {
                return
            }

            _departuresQuery.value = update
        }
    }
/*
    fun setSailingQuery(departureDate: Date) {

        val routeId = _sailingQuery.value?.routeId
        val departingId = _sailingQuery.value?.departingId
        val arrivingId = _sailingQuery.value?.arrivingId

        if (routeId != null && departingId != null && arrivingId != null){
            val update = SailingQuery(
                routeId,
                departingId,
                arrivingId,
                sailingDate
            )
            if (_sailingQuery.value == update) {
                return
            }

            _sailingQuery.value = update
        }

    }

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
