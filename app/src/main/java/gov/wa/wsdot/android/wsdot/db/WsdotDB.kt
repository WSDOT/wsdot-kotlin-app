package gov.wa.wsdot.android.wsdot.db

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossingDao
import gov.wa.wsdot.android.wsdot.db.ferries.*
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPassCameraTypeConverters
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPassDao
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.db.socialmedia.TweetDao
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTableDao
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSignDao
import gov.wa.wsdot.android.wsdot.db.traffic.*
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsRelease
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsReleaseDao
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTimeDao

/**
 * Main database description.
 */
@Database(
    entities = [
        HighwayAlert::class,
        FerrySchedule::class,
        FerrySailing::class,
        FerryAlert::class,
        FerrySpace::class,
        Vessel::class,
        Camera::class,
        MountainPass::class,
        NewsRelease::class,
        TravelTime::class,
        FavoriteLocation::class,
        BorderCrossing::class,
        Tweet::class,
        TollRateTable::class,
        TollSign::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(WsdotTypeConverters::class, MountainPassCameraTypeConverters::class)
abstract class WsdotDB : RoomDatabase() {

    abstract fun highwayAlertDao(): HighwayAlertDao

    abstract fun ferryScheduleDao(): FerryScheduleDao

    abstract fun ferrySailingDao(): FerrySailingDao

    abstract fun ferryAlertDao(): FerryAlertDao

    abstract fun ferrySpaceDao(): FerrySpaceDao

    abstract fun ferrySailingWithSpacesDao(): FerrySailingWithSpacesDao

    abstract fun vesselDao(): VesselDao

    abstract fun mountainPassDao(): MountainPassDao

    abstract fun cameraDao(): CameraDao

    abstract fun newsReleaseDao(): NewsReleaseDao

    abstract fun travelTimeDao(): TravelTimeDao

    abstract fun favoriteLocationDao(): FavoriteLocationDao

    abstract fun borderCrossingDao(): BorderCrossingDao

    abstract fun tweetDao(): TweetDao

    abstract fun tollRateTableDao(): TollRateTableDao

    abstract fun tollSignDao(): TollSignDao

}