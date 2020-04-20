package gov.wa.wsdot.android.wsdot.db.tollrates


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTableDao
import gov.wa.wsdot.android.wsdot.utils.SocialMediaTestUtil
import gov.wa.wsdot.android.wsdot.utils.TollRatesTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class TollRateTableDaoTest {

    private lateinit var tollRateTableDao: TollRateTableDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        tollRateTableDao = db.tollRateTableDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val table = TollRatesTestUtil.createTollRateTable(0)

        tollRateTableDao.insertNewTollRateTable(listOf(
            table,
            TollRatesTestUtil.createTollRateTable(1)
        ))

        val tableFromDb = tollRateTableDao.loadTollRateTableForRoute(0).getOrAwaitValue()

        assertThat(tableFromDb, notNullValue())
        assertThat(tableFromDb, equalTo(table))

    }

    @Test
    fun deleteAll() {


        tollRateTableDao.insertNewTollRateTable(listOf(
            TollRatesTestUtil.createTollRateTable(1),
            TollRatesTestUtil.createTollRateTable(2),
            TollRatesTestUtil.createTollRateTable(3)
        ))

        val table1FromDb = tollRateTableDao.loadTollRateTableForRoute(1).getOrAwaitValue()
        assertThat(table1FromDb, notNullValue())

        val table2FromDb = tollRateTableDao.loadTollRateTableForRoute(2).getOrAwaitValue()
        assertThat(table2FromDb, notNullValue())

        val table3FromDb = tollRateTableDao.loadTollRateTableForRoute(3).getOrAwaitValue()
        assertThat(table3FromDb, notNullValue())

        tollRateTableDao.deleteOldTollRateTable()

        val nullTable1FromDb = tollRateTableDao.loadTollRateTableForRoute(1).getOrAwaitValue()
        assertThat(nullTable1FromDb, nullValue())

        val nullTable2FromDb = tollRateTableDao.loadTollRateTableForRoute(2).getOrAwaitValue()
        assertThat(nullTable2FromDb, nullValue())

        val nullTable3FromDb = tollRateTableDao.loadTollRateTableForRoute(3).getOrAwaitValue()
        assertThat(nullTable3FromDb, nullValue())

    }


}