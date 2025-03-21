package com.morovez.currencyconverter.di

import android.content.Context
import com.morovez.currencyconverter.ui.common.ResourceProvider
import com.morovez.currencyconverter.ui.common.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilModule {
    @Provides
    @Singleton
    fun provideResourceProvider(context: Context): ResourceProvider = ResourceProviderImpl(context)
}