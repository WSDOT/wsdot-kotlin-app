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


import dagger.Module
import dagger.android.ContributesAndroidInjector
import gov.wa.wsdot.android.wsdot.ui.about.AboutFragment
import gov.wa.wsdot.android.wsdot.ui.amtrakcascades.AmtrakCascadesFragment
import gov.wa.wsdot.android.wsdot.ui.amtrakcascades.amtrakcascadesschedule.AmtrakCascadesScheduleFragment
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.BorderCrossingsFragment
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes.NorthboundCrossingTimesFragment
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.crossingtimes.SouthboundCrossingTimesFragment
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraFragment
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListFragment
import gov.wa.wsdot.android.wsdot.ui.eventbanner.EventDetailsFragment
import gov.wa.wsdot.android.wsdot.ui.favorites.FavoritesFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.FerriesHomeFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.FerriesRouteFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertDetailsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras.TerminalCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselDetailsFragment
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselWatchFragment
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.MountainPassHomeFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.MountainPassReportFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passCameras.PassCamerasListFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passConditions.PassConditionsFragment
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.passForecast.PassForecastListFragment
import gov.wa.wsdot.android.wsdot.ui.notifications.NotificationsFragment
import gov.wa.wsdot.android.wsdot.ui.settings.FavoritesSortSettingFragment
import gov.wa.wsdot.android.wsdot.ui.socialmedia.TwitterFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.TollRatesFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.I405TollSignsFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.SR167TollSignsFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.TollTripFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR16TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR520TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.SR99TollTableFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts.MapHighwayAlertsFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.TrafficMapFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.gotolocation.GoToLocationBottomSheetFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.newsrelease.NewsReleaseFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.restareas.RestAreaFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts.HighwayAlertTabFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts.HighestAlertsFragment
import gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts.TravelChartsFragment
import gov.wa.wsdot.android.wsdot.ui.traveltimes.TravelTimeListFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeTrafficMapFragment(): TrafficMapFragment

    @ContributesAndroidInjector
    abstract fun contributeRestAreaFragment(): RestAreaFragment

    @ContributesAndroidInjector
    abstract fun contributeHighwayAlertFragment(): HighwayAlertFragment

    @ContributesAndroidInjector
    abstract fun contributeHighwayAlertTabFragment(): HighwayAlertTabFragment

    @ContributesAndroidInjector
    abstract fun contributeMapHighwayAlertsFragment(): MapHighwayAlertsFragment

    @ContributesAndroidInjector
    abstract fun contributeStatewideAlertsFragment(): HighestAlertsFragment

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
    abstract fun contributeFerryAlertDetailsFragment(): FerryAlertDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeMountainPassHomeFragment(): MountainPassHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeMountainPassReportFragment(): MountainPassReportFragment

    @ContributesAndroidInjector
    abstract fun contributePassConditionsFragment(): PassConditionsFragment

    @ContributesAndroidInjector
    abstract fun contributePassForecastListFragment(): PassForecastListFragment

    @ContributesAndroidInjector
    abstract fun contributePassCamerasListFragment(): PassCamerasListFragment

    @ContributesAndroidInjector
    abstract fun contributeTravelTimeListFragment(): TravelTimeListFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsReleaseFragment(): NewsReleaseFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoritesFragment(): FavoritesFragment

    @ContributesAndroidInjector
    abstract fun contributeBorderCrossingsFragment(): BorderCrossingsFragment

    @ContributesAndroidInjector
    abstract fun contributeNorthboundCrossingTimesFragment(): NorthboundCrossingTimesFragment

    @ContributesAndroidInjector
    abstract fun contributeSouthboundCrossingTimesFragment(): SouthboundCrossingTimesFragment

    @ContributesAndroidInjector
    abstract fun contributeTollRatesFragment(): TollRatesFragment

    @ContributesAndroidInjector
    abstract fun contributeSR520TollTableFragment(): SR520TollTableFragment

    @ContributesAndroidInjector
    abstract fun contributeSR16TollTableFragment(): SR16TollTableFragment

    @ContributesAndroidInjector
    abstract fun contributeSR99TollTableFragment(): SR99TollTableFragment

    @ContributesAndroidInjector
    abstract fun contributeI405TollSignsFragment(): I405TollSignsFragment

    @ContributesAndroidInjector
    abstract fun contributeSR167TollSignsFragment(): SR167TollSignsFragment

    @ContributesAndroidInjector
    abstract fun contributeTwitterFragment(): TwitterFragment

    @ContributesAndroidInjector
    abstract fun contributeTollTripFragment(): TollTripFragment

    @ContributesAndroidInjector
    abstract fun contributeAmtrakCascadesFragment(): AmtrakCascadesFragment

    @ContributesAndroidInjector
    abstract fun contributeAmtrakCascadesScheduleFragment(): AmtrakCascadesScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeEventDetailsFragment(): EventDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeTravelChartsFragment(): TravelChartsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoritesSortSettingFragment(): FavoritesSortSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeGoToLocationBottomSheetFragment(): GoToLocationBottomSheetFragment

}