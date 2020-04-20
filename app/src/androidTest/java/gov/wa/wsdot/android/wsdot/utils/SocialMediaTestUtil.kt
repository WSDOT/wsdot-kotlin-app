package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import java.util.*

object SocialMediaTestUtil {

    fun createTweet(id: String, userId: String = "") = Tweet(
        tweetId = "",
        userId = userId,
        userName = "",
        text = "",
        mediaUrl = null,
        createdAt = Date(),
        localCacheDate = Date()
    )
}