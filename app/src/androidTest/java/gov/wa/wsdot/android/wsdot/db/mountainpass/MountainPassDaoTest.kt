package gov.wa.wsdot.android.wsdot.db.mountainpass

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.MountainPassTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class MountainPassDaoTest {
    private lateinit var mountainPassDao: MountainPassDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        mountainPassDao = db.mountainPassDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val passOneId = 1

        val pass1 = MountainPassTestUtil.createMountainPass(passOneId, "test pass one")
        val pass2 = MountainPassTestUtil.createMountainPass(2, "test pass two")

        mountainPassDao.insertNewPasses(listOf(pass1, pass2))

        val passById = mountainPassDao.loadPass(passOneId).getOrAwaitValue()

        assertThat(passById, notNullValue())
        assertThat(passById, equalTo(pass1))

    }

    @Test
    fun insertAndReadAndDelete() {

        val passOneId = 1

        val pass1 = MountainPassTestUtil.createMountainPass(passOneId, "test pass one")

        mountainPassDao.insertNewPasses(listOf(pass1))

        val passById = mountainPassDao.loadPass(passOneId).getOrAwaitValue()

        assertThat(passById, notNullValue())
        assertThat(passById, equalTo(pass1))

        mountainPassDao.markPassesForRemoval()
        mountainPassDao.deleteOldPasses()

        val nullPass = mountainPassDao.loadPass(passOneId).getOrAwaitValue()

        assertThat(nullPass, nullValue())

    }

    @Test
    fun updateFavorite() {

        val pass1 = MountainPassTestUtil.createMountainPass(1)
        val pass2 = MountainPassTestUtil.createMountainPass(2)
        val pass3 = MountainPassTestUtil.createMountainPass(3)

        mountainPassDao.insertNewPasses(listOf(pass1, pass2, pass3))
        mountainPassDao.updateFavorite(2, true)
        mountainPassDao.updateFavorite(3, true)

        val favoritePasses = mountainPassDao.loadFavoritePasses().getOrAwaitValue()
        assertThat(favoritePasses.first().favorite, equalTo(true))
        assertThat(favoritePasses.size, equalTo(2))

    }

}