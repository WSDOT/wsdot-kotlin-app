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
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraFragment
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.FerriesRouteFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras.TerminalCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselDetailsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselWatchFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.MountainPassHomeFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.MountainPassReportFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.mountainPassConditions.MountainPassConditionsFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passCameras.PassCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.TrafficMapFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeTrafficMapFragment(): TrafficMapFragment

    @ContributesAndroidInjector
    abstract fun contributeCameraFragment(): CameraFragment

    @ContributesAndroidInjector
    abstract fun contributeCameraListFragment(): CameraListFragment

    @ContributesAndroidInjector
    abstract fun contributeFerriesHomeFragment(): FerriesHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeFerriesRouteFragment(): FerriesRouteFragment

    @ContributesAndroidInjector
    abstract fun contributeFerriesSailingFragment(): FerriesSailingFragment

    @ContributesAndroidInjector
    abstract fun contributeTerminalCamerasListFragment(): TerminalCamerasListFragment

    @ContributesAndroidInjector
    abstract fun contributeVesselWatchFragment(): VesselWatchFragment

    @ContributesAndroidInjector
    abstract fun contributeVesselDetailsFragment(): VesselDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeFerryAlertsFragment(): FerryAlertsFragment

    @ContributesAndroidInjector
    abstract fun contributeMountainPassHomeFragment(): MountainPassHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeMountainPassReportFragment(): MountainPassReportFragment

    @ContributesAndroidInjector
    abstract fun contributeMountainPassConditionsFragment(): MountainPassConditionsFragment

    @ContributesAndroidInjector
    abstract fun contributePassCamerasListFragment(): PassCamerasListFragment

}