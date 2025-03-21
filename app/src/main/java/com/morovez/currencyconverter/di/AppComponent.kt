package com.morovez.currencyconverter.di

import android.content.Context
import com.morovez.currencyconverter.ui.MainActivity
import com.morovez.currencyconverter.ui.MainViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        DataModule::class,
        DomainModule::class,
        ApiModule::class,
        UtilModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(activity: MainActivity)

    fun inject(viewModel: MainViewModel)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }
}

object AppComponentHolder {
    private var component: AppComponent? = null

    fun get(): AppComponent {
        return component ?: throw IllegalStateException("Component must be set")
    }

    fun set(component: AppComponent?) {
        this.component = component
    }

    fun build(context: Context): AppComponent {
        return DaggerAppComponent.factory().create(context)
    }
}