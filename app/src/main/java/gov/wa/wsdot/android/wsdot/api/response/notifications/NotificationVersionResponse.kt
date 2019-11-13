package gov.wa.wsdot.android.wsdot.api.response.notifications

import com.google.gson.annotations.SerializedName

data class NotificationVersionResponse (
    @field:SerializedName("version")
    val version: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String
)