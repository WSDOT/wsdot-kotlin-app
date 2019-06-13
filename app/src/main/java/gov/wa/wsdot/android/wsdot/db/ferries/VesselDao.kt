package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class VesselDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertVessels(vessels: List<Vessel>)

    @Query("SELECT * FROM Vessel")
    abstract fun loadVessels(): LiveData<List<Vessel>>

    @Query("SELECT * FROM Vessel WHERE Vessel.vesselId = :vesselId")
    abstract fun loadVessel(vesselId: Int): LiveData<Vessel>

    @Query("DELETE FROM Vessel")
    abstract fun deleteOldVessels()

    @Transaction
    open fun updateVessels(vessels: List<Vessel>) {
        deleteOldVessels()
        insertVessels(vessels)
    }

}