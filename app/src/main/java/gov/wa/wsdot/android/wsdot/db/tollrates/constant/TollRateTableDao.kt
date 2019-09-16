package gov.wa.wsdot.android.wsdot.db.tollrates.constant

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class TollRateTableDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewTollRateTable(tollTable: List<TollRateTable>)

    @Query("SELECT * FROM TollRateTable WHERE route = :route")
    abstract fun loadTollRateTableForRoute(route: Int): LiveData<TollRateTable>

    @Query("DELETE FROM TollRateTable")
    abstract fun deleteOldTollRateTable()

    @Transaction
    open fun updateTollRateTables(items: List<TollRateTable>) {
        deleteOldTollRateTable()
        insertNewTollRateTable(items)
    }


}