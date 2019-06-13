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
import gov.wa.wsdot.android.wsdot.db.ferries.*

/**
 * Main database description.
 */
@Database(
    entities = [
        FerrySchedule::class,
        FerrySailing::class,
        FerryAlert::class,
        FerrySpace::class,
        Vessel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(WsdotTypeConverters::class)
abstract class WsdotDB : RoomDatabase() {

    abstract fun ferryScheduleDao(): FerryScheduleDao

    abstract fun ferrySailingDao(): FerrySailingDao

    abstract fun ferryAlertDao(): FerryAlertDao

    abstract fun ferrySpaceDao(): FerrySpaceDao

    abstract fun ferrySailingWithSpacesDao(): FerrySailingWithSpacesDao

    abstract fun vesselDao(): VesselDao

}