package gov.wa.wsdot.android.wsdot.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.db.ferries.*
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPassDao
import gov.wa.wsdot.android.wsdot.db.traffic.CameraDao
import gov.wa.wsdot.android.wsdot.db.traffic.FavoriteLocationDao
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlertDao
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsRelease
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsReleaseDao
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
    fun provideWsdotApiService(): WsdotApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.wsdot.wa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(WsdotApiService::class.java)
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
    fun highwayAlertDao(db: WsdotDB): HighwayAlertDao {
        return db.highwayAlertDao()
    }

    @Singleton
    @Provides
    fun provideFerryScheduleDao(db: WsdotDB): FerryScheduleDao {
        return db.ferryScheduleDao()
    }

    @Singleton
    @Provides
    fun provideFerrySailingDao(db: WsdotDB): FerrySailingDao {
        return db.ferrySailingDao()
    }

    @Singleton
    @Provides
    fun provideFerryAlertDao(db: WsdotDB): FerryAlertDao {
        return db.ferryAlertDao()
    }

    @Singleton
    @Provides
    fun provideFerrySpacesDao(db: WsdotDB): FerrySpaceDao {
        return db.ferrySpaceDao()
    }

    @Singleton
    @Provides
    fun provideVesselDao(db: WsdotDB): VesselDao {
        return db.vesselDao()
    }

    @Singleton
    @Provides
    fun provideFerrySailingWithSpacesDao(db: WsdotDB): FerrySailingWithSpacesDao {
        return db.ferrySailingWithSpacesDao()
    }

    @Singleton
    @Provides
    fun provideMountainPassDao(db: WsdotDB): MountainPassDao {
        return db.mountainPassDao()
    }

    @Singleton
    @Provides
    fun provideCameraDao(db: WsdotDB): CameraDao {
        return db.cameraDao()
    }

    @Singleton
    @Provides
    fun provideNewsReleaseDao(db: WsdotDB): NewsReleaseDao {
        return db.newsReleaseDao()
    }

    @Singleton
    @Provides
    fun provideFavoriteLocationDao(db: WsdotDB): FavoriteLocationDao {
        return db.favoriteLocationDao()
    }

}