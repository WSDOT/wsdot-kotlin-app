package gov.wa.wsdot.android.wsdot.repository

import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.ApiResponse
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.model.amtrakCascades.AmtrakStop
import gov.wa.wsdot.android.wsdot.util.ApiKeys
import gov.wa.wsdot.android.wsdot.util.network.NetworkResource
import gov.wa.wsdot.android.wsdot.util.network.Resource


@Singleton
class AmtrakCascadesRepository @Inject constructor(
    private val wsdotWebservice: WsdotApiService,
    private val appExecutors: AppExecutors
) {

    fun getDestination(): LiveData<Resource<List<AmtrakStop>>> {
        return object : NetworkResource<List<AmtrakStop>, List<AmtrakScheduleResponse>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<List<AmtrakScheduleResponse>>> {
                return wsdotWebservice.getAmtrackSchedule(apiKey = ApiKeys.WSDOT_KEY, "", "", "")
            }
        }.asLiveData()
    }


    private fun saveVessels(vesselResponse: List<VesselResponse>) {

        var dbVesselList = arrayListOf<Vessel>()

        for (vesselItem in vesselResponse) {

            var schDepart: Date? = null
            if (vesselItem.scheduledDeparture != null) {
                schDepart =  Date(vesselItem.scheduledDeparture.substring(6, 19).toLong())
            }

            var leftDock: Date? = null
            if (vesselItem.leftDock != null){
                leftDock = Date(vesselItem.leftDock.substring(6, 19).toLong())
            }

            var eta: Date? = null
            if (vesselItem.eta != null) {
                eta = Date(vesselItem.eta.substring(6, 19).toLong())
            }

            var vessel = Vessel(
                vesselItem.vesselId,
                vesselItem.vesselName,
                vesselItem.departingTerminalId,
                vesselItem.departingTerminalName,
                vesselItem.arrivingTerminalId,
                vesselItem.arrivingTerminalName,
                vesselItem.latitude,
                vesselItem.longitude,
                vesselItem.speed,
                vesselItem.heading,
                vesselItem.inService,
                vesselItem.atDock,
                schDepart,
                leftDock,
                eta,
                vesselItem.etaBasis,
                Date(),
                Date(vesselItem.timeStamp.substring(6, 19).toLong())
            )

            dbVesselList.add(vessel)

        }

        vesselDao.updateVessels(dbVesselList)
    }



}