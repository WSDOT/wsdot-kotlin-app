package gov.wa.wsdot.android.wsdot.db.ferries

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.utils.FerriesTestUtil
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class FerryAlertDaoTest {
    private lateinit var ferryAlertDao: FerryAlertDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        ferryAlertDao = db.ferryAlertDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val alert1 = FerriesTestUtil.createFerryAlerts(2, 4)

        ferryAlertDao.insertAlerts(listOf(alert1))

        val alert = ferryAlertDao.loadAlertById(2).getOrAwaitValue()

        assertThat(alert, notNullValue())
        assertThat(alert, equalTo(alert1))
    }


    @Test
    fun multiInsertAndRead() {

        val alert1 = FerriesTestUtil.createFerryAlerts(2, 4)
        val alert2 = FerriesTestUtil.createFerryAlerts(2, 5)
        val alert3 = FerriesTestUtil.createFerryAlerts(2, 6)

        ferryAlertDao.insertAlerts(listOf(alert1, alert2, alert3))

        val alerts = ferryAlertDao.loadAlerts().getOrAwaitValue()
        assertThat(alerts.size, equalTo(3))

    }

    @Test
    fun readRouteAlerts() {
        val alert1 = FerriesTestUtil.createFerryAlerts(2, 4)
        val alert2 = FerriesTestUtil.createFerryAlerts(3, 4)
        val alert3 = FerriesTestUtil.createFerryAlerts(2, 6)
        val alert4 = FerriesTestUtil.createFerryAlerts(3, 6)

        ferryAlertDao.insertAlerts(listOf(alert1, alert2, alert3, alert4))

        val alerts = ferryAlertDao.loadAlertsById(4).getOrAwaitValue()

        assertThat(alerts.size, equalTo(2))

    }

    @Test
    fun duplicateAlerts() {
        val alert1 = FerriesTestUtil.createFerryAlerts(2, 4)
        val alert2 = FerriesTestUtil.createFerryAlerts(2, 4)
        val alert3 = FerriesTestUtil.createFerryAlerts(2, 5)

        ferryAlertDao.insertAlerts(listOf(alert1, alert2, alert3))

        val alerts = ferryAlertDao.loadAlerts().getOrAwaitValue()

        assertThat(alerts.size, equalTo(2))

    }

}