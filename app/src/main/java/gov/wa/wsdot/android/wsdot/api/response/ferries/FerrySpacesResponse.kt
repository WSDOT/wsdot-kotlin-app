package gov.wa.wsdot.android.wsdot.api.response.ferries

import com.google.gson.annotations.SerializedName

data class FerrySpacesResponse (

    @field:SerializedName("TerminalID")
    val terminalId: Int,

    @field:SerializedName("TerminalName")
    val terminalName: String,

    @field:SerializedName("TerminalAbbrev")
    val terminalAbbrev: String,

    @field:SerializedName("DepartingSpaces")
    val departingSpaces: List<DepartingSpaces>

) {

    data class DepartingSpaces(
        @field:SerializedName("Departure")
        val departureTime: String,

        @field:SerializedName("IsCancelled")
        val isCancelled: Boolean,

        @field:SerializedName("VesselID")
        val vesselId: Int,

        @field:SerializedName("VesselName")
        val vesselName: String,

        @field:SerializedName("MaxSpaceCount")
        val maxSpaceCount: Int,

        @field:SerializedName("SpaceForArrivalTerminals")
        val spaceForArrivalTerminals: List<ArrivingSpaces>

    ) {

        data class ArrivingSpaces(


            @field:SerializedName("ArrivalTerminalIDs")
            val arrivalTerminalIds: List<Int>

        )
    }


}

/*

    "SpaceForArrivalTerminals": [
        {
        "TerminalID": 8,
        "TerminalName": "Edmonds",
        "VesselID": 25,
        "VesselName": "Puyallup",
        "DisplayReservableSpace": false,
        "ReservableSpaceCount": null,
        "ReservableSpaceHexColor": null,
        "DisplayDriveUpSpace": true,
        "DriveUpSpaceCount": 53,
        "DriveUpSpaceHexColor": "#FFFF00",
        "MaxSpaceCount": 203,



 */