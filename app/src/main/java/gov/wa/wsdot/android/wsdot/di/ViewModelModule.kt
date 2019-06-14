package gov.wa.wsdot.android.wsdot.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import gov.wa.wsdot.android.wsdot.di.viewmodel.WsdotViewModelFactory
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraViewModel
import gov.wa.wsdot.android.wsdot.ui.common.viewmodel.SharedDateViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.FerriesViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.FerriesRouteViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing.FerriesSailingViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselDetailsViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch.VesselWatchViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindCameraViewModel(cameraViewModel: CameraViewModel): ViewModel

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
    @ViewModelKey(SharedDateViewModel::class)
    abstract fun bindSharedDateViewModel(sharedDateViewModel: SharedDateViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: WsdotViewModelFactory): ViewModelProvider.Factory
}