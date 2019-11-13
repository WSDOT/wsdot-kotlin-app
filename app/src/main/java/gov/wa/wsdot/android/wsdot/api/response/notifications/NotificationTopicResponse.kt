package gov.wa.wsdot.android.wsdot.api.response.notifications

import com.google.gson.annotations.SerializedName

data class NotificationTopicResponse (
    @field:SerializedName("topics")
    val topics: List<TopicItem>
) {
    data class TopicItem(
        @field:SerializedName("topic")
        val topic: String,
        @field:SerializedName("title")
        val title: String,
        @field:SerializedName("category")
        val category: String
    )
}