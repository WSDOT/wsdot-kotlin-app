package gov.wa.wsdot.android.wsdot.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.FerriesTestUtil
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleDao
import gov.wa.wsdot.android.wsdot.getOrAwaitValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class FerryScheduleDaoTest {
    private lateinit var ferryScheduleDao: FerryScheduleDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        ferryScheduleDao = db.ferryScheduleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val schedule1: FerrySchedule = FerriesTestUtil.createFerrySchedule(3, "")
        val schedule2: FerrySchedule = FerriesTestUtil.createFerrySchedule(4, "")

        ferryScheduleDao.insertNewSchedules(listOf(schedule1, schedule2))

        val scheduleById = ferryScheduleDao.loadSchedule(3).getOrAwaitValue()
        assertThat(scheduleById, notNullValue())
        assertThat(scheduleById, equalTo(schedule1))
    }

    @Test
    fun updateScheduleDescription() {

        val desc = "ferry Schedule"

        val schedule1: FerrySchedule = FerriesTestUtil.createFerrySchedule(3, "")
        val schedule2: FerrySchedule = FerriesTestUtil.createFerrySchedule(4, "")

        ferryScheduleDao.insertNewSchedules(listOf(schedule1, schedule2))
        val newSchedule: FerrySchedule = FerriesTestUtil.createFerrySchedule(3, desc)
        ferryScheduleDao.update(listOf(newSchedule))

        val scheduleById = ferryScheduleDao.loadSchedule(3).getOrAwaitValue()
        assertThat(scheduleById.description, equalTo(desc))
    }

    @Test
    fun updateFavorite() {

        val schedule1: FerrySchedule = FerriesTestUtil.createFerrySchedule(3, "")
        val schedule2: FerrySchedule = FerriesTestUtil.createFerrySchedule(4, "")
        val schedule3: FerrySchedule = FerriesTestUtil.createFerrySchedule(5, "")

        db.runInTransaction {
            ferryScheduleDao.insertNewSchedules(listOf(schedule1, schedule2, schedule3))
            ferryScheduleDao.updateFavorite(3, true)
            ferryScheduleDao.updateFavorite(4, true)
        }

        val scheduleByFavorite = ferryScheduleDao.loadFavoriteSchedules().getOrAwaitValue()
        assertThat(scheduleByFavorite.first().favorite,  equalTo(true))
        assertThat(scheduleByFavorite.size, equalTo(2))

    }
}