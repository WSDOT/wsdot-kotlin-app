package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.tollrates.TollRateTableResponse
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateRow
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTableDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TollRateRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val tollRateTableDao: TollRateTableDao
) {

    fun loadTollTableForRoute(route: Int, forceRefresh: Boolean): LiveData<Resource<TollRateTable>> {

        return object : NetworkBoundResource<TollRateTable, TollRateTableResponse>(appExecutors) {

            override fun saveCallResult(item: TollRateTableResponse) = saveTollRateTables(item)

            override fun shouldFetch(data: TollRateTable?): Boolean {

                var update = false

                if (data != null ){
                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 1440)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = tollRateTableDao.loadTollRateTableForRoute(route)

            override fun createCall() = dataWebservice.getTollRateTables()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveTollRateTables(tollRateTableResponse: TollRateTableResponse) {

        val dbTableList = arrayListOf<TollRateTable>()

        for (tollTableItem in tollRateTableResponse.tollRates) {

            val tableRows = arrayListOf<TollRateRow>()

            for (row in tollTableItem.rows) {
                val tollTableRow = TollRateRow(
                    header = row.isHeader,
                    weekday = row.isWeekday,
                    startTime = row.startTime,
                    endTime = row.endTime,
                    values = row.values
                )
                tableRows.add(tollTableRow)
            }

            val tollRateTable = TollRateTable(
                route = tollTableItem.route,
                message = tollTableItem.message,
                numCol = tollTableItem.numCol,
                localCacheDate = Date(),
                rows = tableRows
            )

            dbTableList.add(tollRateTable)
        }
        tollRateTableDao.updateTollRateTables(dbTableList)
    }
}
