package gov.wa.wsdot.android.wsdot.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import gov.wa.wsdot.android.wsdot.WsdotApp
import gov.wa.wsdot.android.wsdot.api.JsonDateDeserializer
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossingDao
import gov.wa.wsdot.android.wsdot.db.ferries.*
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPassDao
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopicDao
import gov.wa.wsdot.android.wsdot.db.socialmedia.TweetDao
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTableDao
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSignDao
import gov.wa.wsdot.android.wsdot.db.traffic.CameraDao
import gov.wa.wsdot.android.wsdot.db.traffic.FavoriteLocationDao
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlertDao
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsReleaseDao
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTimeDao
import gov.wa.wsdot.android.wsdot.util.api.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(app: Application): Context {
        return app
    }

    @Singleton
    @Provides
    fun provideWebService(): WebDataService {

        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, JsonDateDeserializer())
            //.registerTypeAdapter(Date::class.java, DateStringDeserializer())

        return Retrofit.Builder()
            .baseUrl("https://data.wsdot.wa.gov/mobile/")
            .addConverterFactory(GsonConverterFactory.create(gson.create()))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(WebDataService::class.java)
    }

    @Singleton
    @Provides
    fun provideWsdotApiService(): WsdotApiService {

        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, JsonDateDeserializer())

        return Retrofit.Builder()
            .baseUrl("https://www.wsdot.wa.gov/")
            .addConverterFactory(GsonConverterFactory.create(gson.create()))
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
    fun provideTravelTimeDao(db: WsdotDB): TravelTimeDao {
        return db.travelTimeDao()
    }

    @Singleton
    @Provides
    fun provideFavoriteLocationDao(db: WsdotDB): FavoriteLocationDao {
        return db.favoriteLocationDao()
    }

    @Singleton
    @Provides
    fun provideBorderCrossingDao(db: WsdotDB): BorderCrossingDao {
        return db.borderCrossingDao()
    }

    @Singleton
    @Provides
    fun provideTweetDao(db: WsdotDB): TweetDao {
        return db.tweetDao()
    }

    @Singleton
    @Provides
    fun provideTollRateTableDao(db: WsdotDB): TollRateTableDao {
        return db.tollRateTableDao()
    }

    @Singleton
    @Provides
    fun provideTollSignDao(db: WsdotDB): TollSignDao {
        return db.tollSignDao()
    }

    @Singleton
    @Provides
    fun provideNotificationTopicDao(db: WsdotDB): NotificationTopicDao {
        return db.notificationTopicDao()
    }

}