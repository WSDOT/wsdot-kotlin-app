package gov.wa.wsdot.android.wsdot.api.response.traffic

import com.google.gson.annotations.SerializedName



data class CamerasResponse (
    @field:SerializedName("cameras")
    val cameras: Cameras
) {
    data class Cameras (
        @field:SerializedName("items")
        val items: List<CameraItem>
    ) {
        data class CameraItem(
            @field:SerializedName("id")
            val cameraId: Int,
            @field:SerializedName("title")
            val title: String,
            @field:SerializedName("roadName")
            val roadName: String,
            @field:SerializedName("milepost")
            val milepost: Int,
            @field:SerializedName("direction")
            val direction: String?,
            @field:SerializedName("lat")
            val latitude: Double,
            @field:SerializedName("lon")
            val longitude: Double,
            @field:SerializedName("url")
            val url: String,
            @field:SerializedName("video")
            val hasVideo: Int
        )
    }
}