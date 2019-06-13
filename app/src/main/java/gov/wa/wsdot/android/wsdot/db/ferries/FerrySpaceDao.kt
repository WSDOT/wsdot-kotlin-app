package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.*

@Dao
abstract class FerrySpaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSpaces(spaces: List<FerrySpace>)

    @Query("DELETE FROM FerrySpace")
    abstract fun deleteOldSpaces()

    @Transaction
    open fun updateSpaces(spaces: List<FerrySpace>) {
        deleteOldSpaces()
        insertSpaces(spaces)
    }


}