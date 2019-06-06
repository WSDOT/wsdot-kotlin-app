package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
abstract class FerrySpaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSpaces(spaces: List<FerrySpace>)
}