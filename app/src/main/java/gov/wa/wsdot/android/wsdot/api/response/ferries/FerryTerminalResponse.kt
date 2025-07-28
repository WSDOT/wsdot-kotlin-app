package gov.wa.wsdot.android.wsdot.api.response.ferries

import com.google.gson.annotations.SerializedName

data class FerryTerminalResponse (

    @field:SerializedName("TerminalID")
    val terminalID: Int,

    @field:SerializedName("TerminalName")
    val terminalName: String,

    @field:SerializedName("AddressLineOne")
    val addressLineOne: String,

    @field:SerializedName("City")
    val city: String,

    @field:SerializedName("State")
    val state: String,

    @field:SerializedName("ZipCode")
    val zipCode: String,

    @field:SerializedName("Latitude")
    val latitude: Double,

    @field:SerializedName("Longitude")
    val longitude: Double,

    @field:SerializedName("Bulletins")
    val bulletins: List<BulletinAlert>

)
{
    data class BulletinAlert(
        @field:SerializedName("BulletinTitle")
        val bulletinTitle: String,
        @field:SerializedName("BulletinText")
        val bulletinText: String,
        @field:SerializedName("BulletinLastUpdated")
        val bulletinLastUpdated: String
    )

}