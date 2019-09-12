package gov.wa.wsdot.android.wsdot.db.socialmedia

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class TweetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewTweets(tweets: List<Tweet>)

    @Query("SELECT * FROM Tweet")
    abstract fun loadTweets(): LiveData<List<Tweet>>

    @Transaction
    open fun updateTweets(tweets: List<Tweet>) {
        deleteOldTweets()
        insertNewTweets(tweets)
    }

    @Query("DELETE FROM Tweet")
    abstract fun deleteOldTweets()

}