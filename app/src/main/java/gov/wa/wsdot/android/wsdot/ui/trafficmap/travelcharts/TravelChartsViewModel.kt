package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelChartsStatusResponse
import gov.wa.wsdot.android.wsdot.repository.TravelChartsRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TravelChartsViewModel @Inject constructor(travelChartsRepository: TravelChartsRepository) : ViewModel() {

    val repo = travelChartsRepository

    // mediator handles resubscribe on refresh
    val travelCharts = MediatorLiveData<Resource<TravelChartsStatusResponse>>()
    private var travelChartsLiveData: LiveData<Resource<TravelChartsStatusResponse>> = travelChartsRepository.getTravelChartsStatus()

    // holds list of charts to display dependent on selected chart route
    val displayedTravelCharts = MediatorLiveData<List<TravelChartsStatusResponse.ChartRoute.TravelChart>>()

    // 2-way data bind
    val chartRoutes = MediatorLiveData<List<Pair<String, String>>>()

    // 2-way binding value for spinner
    private val _selectedChartRoute = MediatorLiveData<Pair<String, String>>()
    val selectedChartRoute: MutableLiveData<Pair<String, String>>
        get() = _selectedChartRoute

    init {

        travelCharts.addSource(travelChartsLiveData) { travelCharts.value = it}

        // charts to display based on the value of the selected route
        displayedTravelCharts.addSource(_selectedChartRoute) {
            displayedTravelCharts.value = travelCharts.value?.data?.routes?.get(it.second.toInt())?.charts
        }

        // get the initial value ONCE (remove observer) after we've collected all the routes from the source.
        _selectedChartRoute.addSource(chartRoutes) {
            if (it.isNotEmpty()) {
                _selectedChartRoute.value = it[0]
                _selectedChartRoute.removeSource(chartRoutes)
            }
        }

        // get all the possible routes from the data source
        chartRoutes.addSource(travelCharts) {
            chartRoutes.value = parseTravelChartRoutes(it.data)
        }

    }

    // refreshes the repo source
    fun refresh() {
        travelCharts.removeSource(travelChartsLiveData)
        travelChartsLiveData = repo.getTravelChartsStatus()
        travelCharts.addSource(travelChartsLiveData) { travelCharts.value = it }
    }

    // converts server data into  List<Pair<String, String>> with the first string being the display name
    // for the spinner and the second string being the index into the data.
    private fun parseTravelChartRoutes(data: TravelChartsStatusResponse?): List<Pair<String, String>> {
        data?.let { travelCharts ->
            return List(travelCharts.routes.size) { index ->
                Pair(travelCharts.routes[index].name, index.toString())
            }
        }
        return emptyList()
    }

}