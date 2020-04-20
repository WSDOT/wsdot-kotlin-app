package gov.wa.wsdot.android.wsdot.db.traffic

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.utils.FerriesTestUtil
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.TrafficTestUtil
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
class HighwayAlertDaoTest {
    private lateinit var highwayAlertDao: HighwayAlertDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        highwayAlertDao = db.highwayAlertDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val alertId = 1

        val alert1 = TrafficTestUtil.createHighwayAlert(alertId)

        highwayAlertDao.insertNewHighwayAlerts(listOf(alert1))

        val alert = highwayAlertDao.loadHighwayAlert(alertId).getOrAwaitValue()

        assertThat(alert, notNullValue())
        assertThat(alert, equalTo(alert1))
    }

    @Test
    fun insertReadAndDelete() {

        val alertId = 1

        val alert1 = TrafficTestUtil.createHighwayAlert(alertId)

        highwayAlertDao.insertNewHighwayAlerts(listOf(alert1))

        val alert = highwayAlertDao.loadHighwayAlert(alertId).getOrAwaitValue()

        assertThat(alert, notNullValue())
        assertThat(alert, equalTo(alert1))

        highwayAlertDao.deleteOldHighwayAlerts()

        val alerts = highwayAlertDao.loadHighwayAlerts().getOrAwaitValue()

        assertThat(alerts, notNullValue())
        assertThat(alerts.size, equalTo(0))

    }

    @Test
    fun alertInBounds() {

        val alert1 = TrafficTestUtil.createHighwayAlert(
            id = 2,
            startLatitude = 47.599272,
            startLongitude = -122.331656
        )

        highwayAlertDao.insertNewHighwayAlerts(listOf(alert1))

        val alertInBounds = highwayAlertDao.loadHighwayAlertsInBounds(
            minLat = 47.569399,
            maxLat = 47.614601,
            minLng = -122.391737,
            maxLng = -122.295474
        ).getOrAwaitValue().first()

        assertThat(alertInBounds, equalTo(alert1))

    }

    @Test
    fun camerasOutOfBounds() {

        val alert1 = TrafficTestUtil.createHighwayAlert(
            id = 1,
            startLatitude = 47.508031,
            startLongitude = -122.361392
        )

        val alert2 = TrafficTestUtil.createHighwayAlert(
            id = 2,
            startLatitude = 0.0,
            startLongitude = 0.0
        )

        highwayAlertDao.insertNewHighwayAlerts(listOf(alert1, alert2))

        val alertsInBounds = highwayAlertDao.loadHighwayAlertsInBounds(
            minLat = 47.569399,
            maxLat = 47.614601,
            minLng = -122.391737,
            maxLng = -122.295474
        ).getOrAwaitValue()

        assertThat(alertsInBounds.size, equalTo(0))

    }

}