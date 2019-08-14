package gov.wa.wsdot.android.wsdot.api.response.travelerinfo

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

data class NewsReleaseResponse (
    @field:SerializedName("news")
    val news: News
) {
    data class News (
        @field:SerializedName("items")
        val items: List<NewsItem>
    ) {
        data class NewsItem(
            @field:SerializedName("link")
            val link: String,
            @field:SerializedName("title")
            val title: String,
            @field:SerializedName("description")
            val description: String,
            @field:SerializedName("pubdate")
            val pubdate: String
        )
    }
}