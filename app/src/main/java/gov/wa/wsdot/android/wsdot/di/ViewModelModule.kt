package gov.wa.wsdot.android.wsdot.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import gov.wa.wsdot.android.wsdot.di.viewmodel.WsdotViewModelFactory
import gov.wa.wsdot.android.wsdot.ui.EventBannerViewModel
import gov.wa.wsdot.android.wsdot.ui.amtrakcascades.AmtrakCascadesViewModel
import gov.wa.wsdot.android.wsdot.ui.bordercrossings.BorderCrossingViewModel
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListViewModel
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraViewModel
import gov.wa.wsdot.android.wsdot.ui.cameras.CamerasViewModel
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import gov.wa.wsdot.android.wsdot.ui.favorites.FavoritesListViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.FerriesViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.FerriesRouteViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertDetailsViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts.FerryAlertsViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras.TerminalCamerasViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselDetailsViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselWatchViewModel
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertViewModel
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertsViewModel
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.MountainPassViewModel
import gov.wa.wsdot.android.wsdot.ui.mountainpasses.report.MountainPassReportViewModel
import gov.wa.wsdot.android.wsdot.ui.notifications.NotificationsViewModel
import gov.wa.wsdot.android.wsdot.ui.socialmedia.TwitterViewModel
import gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns.TollSignsViewModel
import gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable.TollRateTableViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.MapCamerasViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts.MapHighwayAlertsViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.favoriteLocation.FavoriteLocationViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.newsrelease.NewsReleaseViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.restareas.RestAreaViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts.HighestImpactAlertsViewModel
import gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts.TravelChartsViewModel
import gov.wa.wsdot.android.wsdot.ui.traveltimes.TravelTimeListViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindCameraViewModel(cameraViewModel: CameraViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CamerasViewModel::class)
    abstract fun bindCamerasViewModel(camerasViewModel: CamerasViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CameraListViewModel::class)
    abstract fun bindCameraListViewModel(cameraViewModel: CameraListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapCamerasViewModel::class)
    abstract fun bindMapCamerasViewModel(mapCamerasViewModel: MapCamerasViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RestAreaViewModel::class)
    abstract fun bindRestAreaViewModel(restAreaViewModel: RestAreaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HighwayAlertViewModel::class)
    abstract fun bindHighwayAlertViewModel(highwayAlertViewModel: HighwayAlertViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HighwayAlertsViewModel::class)
    abstract fun bindHighwayAlertsViewModel(highwayAlertsViewModel: HighwayAlertsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapHighwayAlertsViewModel::class)
    abstract fun bindMapHighwayAlertsViewModel(mapHighwayAlertsViewModel: MapHighwayAlertsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HighestImpactAlertsViewModel::class)
    abstract fun bindStatewideAlertsViewModel(statewideAlertsViewModel: HighestImpactAlertsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TerminalCamerasViewModel::class)
    abstract fun bindTerminalCamerasViewModel(terminalCamerasViewModel: TerminalCamerasViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FerriesViewModel::class)
    abstract fun bindFerriesViewModel(ferriesViewModel: FerriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FerriesRouteViewModel::class)
    abstract fun bindFerriesRouteViewModel(ferriesRouteViewModel: FerriesRouteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FerriesSailingViewModel::class)
    abstract fun bindFerriesSailingViewModel(ferriesSailingViewModel: FerriesSailingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VesselWatchViewModel::class)
    abstract fun bindVesselWatchViewModel(vesselWatchViewModel: VesselWatchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VesselDetailsViewModel::class)
    abstract fun bindVesselDetailsViewModel(vesselDetailsViewModel: VesselDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FerryAlertsViewModel::class)
    abstract fun bindFerryAlertsViewModel(ferryAlertsViewModel: FerryAlertsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MountainPassViewModel::class)
    abstract fun bindMountainPassViewModel(mountainPassViewModel: MountainPassViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MountainPassReportViewModel::class)
    abstract fun bindMountainPassReportViewModel(mountainPassReportViewModel: MountainPassReportViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsReleaseViewModel::class)
    abstract fun bindNewsReleaseViewModel(newsReleaseViewModel: NewsReleaseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteLocationViewModel::class)
    abstract fun bindFavoriteLocationViewModel(favoriteLocationViewModel: FavoriteLocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TravelTimeListViewModel::class)
    abstract fun bindTravelTimeListViewModel(travelTimeListViewModel: TravelTimeListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesListViewModel::class)
    abstract fun bindFavoritesListViewModel(favoritesListViewModel: FavoritesListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedDateViewModel::class)
    abstract fun bindSharedDateViewModel(sharedDateViewModel: SharedDateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BorderCrossingViewModel::class)
    abstract fun bindBorderCrossingViewModel(borderCrossingViewModel: BorderCrossingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TwitterViewModel::class)
    abstract fun findTwitterViewModel(twitterViewModel: TwitterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TollRateTableViewModel::class)
    abstract fun findTollRateTableViewModel(tollRateTableViewModel: TollRateTableViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TollSignsViewModel::class)
    abstract fun findTollSignsViewModel(tollSignsViewModel: TollSignsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AmtrakCascadesViewModel::class)
    abstract fun findAmtrakCascadesViewModel(amtrakCascadesViewModel: AmtrakCascadesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EventBannerViewModel::class)
    abstract fun findEventBannerViewModel(mainViewModel: EventBannerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TravelChartsViewModel::class)
    abstract fun findTravelChartsViewModel(travelChartsViewModel: TravelChartsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun findNotificationsViewModel(notificationsViewModel: NotificationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FerryAlertDetailsViewModel::class)
    abstract fun findFerryAlertDetailsViewModel(ferryAlertDetailsViewModel: FerryAlertDetailsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: WsdotViewModelFactory): ViewModelProvider.Factory
}