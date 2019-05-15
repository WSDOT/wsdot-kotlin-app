package gov.wa.wsdot.android.wsdot.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import gov.wa.wsdot.android.wsdot.di.viewmodel.WsdotViewModelFactory
import gov.wa.wsdot.android.wsdot.ui.ferries.FerriesViewModel
import gov.wa.wsdot.android.wsdot.ui.ferries.FerryAlertsViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FerriesViewModel::class)
    abstract fun bindFerriesViewModel(ferriesViewModel: FerriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FerryAlertsViewModel::class)
    abstract fun bindFerryAlertsViewModel(ferryAlertsViewModel: FerryAlertsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: WsdotViewModelFactory): ViewModelProvider.Factory
}