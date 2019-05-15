package gov.wa.wsdot.android.wsdot.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleDao
import gov.wa.wsdot.android.wsdot.util.api.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideWebService(): WebDataService {
        return Retrofit.Builder()
            .baseUrl("https://data.wsdot.wa.gov/mobile/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(WebDataService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): WsdotDB {
        return Room
            .databaseBuilder(app, WsdotDB::class.java, "wsdot.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: WsdotDB): FerryScheduleDao{
        return db.ferryScheduleDao()
    }

}