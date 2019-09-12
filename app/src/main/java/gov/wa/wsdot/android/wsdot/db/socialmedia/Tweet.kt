package gov.wa.wsdot.android.wsdot.db.socialmedia

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["tweetId"])
data class Tweet(
    val tweetId: Float,
    val userId: Float,
    val userName: String,
    val text: String,
    val mediaUrl: String?,
    val createdAt: Date,
    val localCacheDate: Date
)