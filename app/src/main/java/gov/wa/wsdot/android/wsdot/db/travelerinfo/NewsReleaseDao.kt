package gov.wa.wsdot.android.wsdot.db.travelerinfo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class NewsReleaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewReleases(newsItems: List<NewsRelease>)

    @Query("SELECT * FROM NewsRelease")
    abstract fun loadNewsReleases(): LiveData<List<NewsRelease>>

    @Query("DELETE FROM NewsRelease")
    abstract fun deleteOldNewsReleases()

    @Transaction
    open fun updateNewsReleases(news: List<NewsRelease>) {
        deleteOldNewsReleases()
        insertNewReleases(news)
    }


}