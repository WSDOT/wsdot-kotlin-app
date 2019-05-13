package gov.wa.wsdot.android.wsdot.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import gov.wa.wsdot.android.wsdot.ui.home.MainActivity

@Suppress("unused")
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}