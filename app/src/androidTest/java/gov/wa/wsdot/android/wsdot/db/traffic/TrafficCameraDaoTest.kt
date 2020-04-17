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
class TrafficCameraDaoTest {
    private lateinit var cameraDao: CameraDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        cameraDao = db.cameraDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val cameraId = 2

        val camera1 = TrafficTestUtil.createCamera(cameraId)

        cameraDao.insertNewCameras(listOf(camera1))

        val camera = cameraDao.loadCamerasWithIds(listOf(cameraId)).getOrAwaitValue().first()

        assertThat(camera, notNullValue())
        assertThat(camera, equalTo(camera1))
    }

    @Test
    fun loadCamerasOnRoad() {

        val camera1 = TrafficTestUtil.createCamera(id = 1, roadName = "5")
        val camera2 = TrafficTestUtil.createCamera(id = 2, roadName = "405")
        val camera3 = TrafficTestUtil.createCamera(id = 3, roadName = "90")
        val camera4 = TrafficTestUtil.createCamera(id = 4, roadName = "90")

        cameraDao.insertNewCameras(listOf(camera1, camera2, camera3, camera4))

        val camerasOnI5 = cameraDao.loadCamerasOnRoad("5").getOrAwaitValue()

        assertThat(camerasOnI5.first(), equalTo(camera1))

    }

    @Test
    fun camerasInBounds() {

        val camera1 = TrafficTestUtil.createCamera(id = 1, latitude = 47.599272, longitude = -122.331656)

        cameraDao.insertNewCameras(listOf(camera1))

        val cameraInBounds = cameraDao.loadCamerasInBounds(
            minLat = 47.569399,
            maxLat = 47.614601,
            minLng = -122.391737,
            maxLng = -122.295474
        ).getOrAwaitValue().first()

        assertThat(cameraInBounds, equalTo(camera1))

    }

    @Test
    fun camerasOutOfBounds() {

        val camera1 = TrafficTestUtil.createCamera(
            id = 1,
            latitude = 47.508031,
            longitude = -122.361392
        )

        val camera2 = TrafficTestUtil.createCamera(
            id = 2,
            latitude = 0.0,
            longitude = 0.0
        )

        cameraDao.insertNewCameras(listOf(camera1, camera2))

        val camerasInBounds = cameraDao.loadCamerasInBounds(
            minLat = 47.569399,
            maxLat = 47.614601,
            minLng = -122.391737,
            maxLng = -122.295474
        ).getOrAwaitValue()

        assertThat(camerasInBounds.size, equalTo(0))

    }

    @Test
    fun deleteAll() {

        val camera1 = TrafficTestUtil.createCamera(1)
        val camera2 = TrafficTestUtil.createCamera(2)
        val camera3 = TrafficTestUtil.createCamera(3)
        val camera4 = TrafficTestUtil.createCamera(4)

        cameraDao.insertNewCameras(listOf(camera1, camera2, camera3, camera4))

        cameraDao.markCamerasForRemoval()
        cameraDao.deleteOldCameras()

        val cameras = cameraDao.loadCameras().getOrAwaitValue()

        assertThat(cameras.size, equalTo(0))

    }

    @Test
    fun updateFavorite() {

        val camera1 = TrafficTestUtil.createCamera(1)
        val camera2 = TrafficTestUtil.createCamera(2)
        val camera3 = TrafficTestUtil.createCamera(3)

        cameraDao.insertNewCameras(listOf(camera1, camera2, camera3))

        cameraDao.updateFavorite(1, true)
        cameraDao.updateFavorite(2, true)

        val favoriteCameras = cameraDao.loadFavoriteCameras().getOrAwaitValue()

        assertThat(favoriteCameras.size, equalTo(2))
        assertThat(favoriteCameras.first().favorite, equalTo(true))

    }
}