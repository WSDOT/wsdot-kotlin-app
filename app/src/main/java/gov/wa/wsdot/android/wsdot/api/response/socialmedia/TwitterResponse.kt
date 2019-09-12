package gov.wa.wsdot.android.wsdot.api.response.socialmedia

import com.google.gson.annotations.SerializedName

data class TwitterResponse (
    @field:SerializedName("id")
    val id: Float,
    @field:SerializedName("text")
    val text: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("entities")
    val entities: Entities,
    @field:SerializedName("user")
    val user: User
) {
    data class User(
        @field:SerializedName("id")
        val id: Float,
        @field:SerializedName("name")
        val name: String
    )
    data class Entities(
        @field:SerializedName("media")
        val media: List<MediaContent>?
    ) {
        data class MediaContent(
            @field:SerializedName("media_url_https")
            val mediaUrl: String
        )
    }
}