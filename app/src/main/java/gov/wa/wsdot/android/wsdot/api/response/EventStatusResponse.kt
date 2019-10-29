package gov.wa.wsdot.android.wsdot.api.response

import com.google.gson.annotations.SerializedName

data class EventStatusResponse (
    @field:SerializedName("startDate")
    val startDate: String,
    @field:SerializedName("endDate")
    val endDate: String,
    @field:SerializedName("themeId")
    val themeId: Int,
    @field:SerializedName("bannerText")
    val bannerText: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("details")
    val details: String
)