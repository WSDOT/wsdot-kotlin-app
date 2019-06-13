package gov.wa.wsdot.android.wsdot.di

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


import gov.wa.wsdot.android.wsdot.ui.ferries.FerriesHomeFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector
import gov.wa.wsdot.android.wsdot.ui.ferries.route.FerriesRouteFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselDetailsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselWatchFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.TrafficMapFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeTrafficMapFragment(): TrafficMapFragment

    @ContributesAndroidInjector
    abstract fun contributeFerriesHomeFragment(): FerriesHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeFerriesRouteFragment(): FerriesRouteFragment

    @ContributesAndroidInjector
    abstract fun contributeFerriesSailingFragment(): FerriesSailingFragment

    @ContributesAndroidInjector
    abstract fun contributeVesselWatchFragment(): VesselWatchFragment

    @ContributesAndroidInjector
    abstract fun contributeVesselDetailsFragment(): VesselDetailsFragment

}