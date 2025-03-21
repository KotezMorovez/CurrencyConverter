package com.morovez.currencyconverter.di

import com.morovez.currencyconverter.data.account.repository.AccountRepositoryImpl
import com.morovez.currencyconverter.data.account.service.AccountStorage
import com.morovez.currencyconverter.data.account.service.AccountStorageImpl
import com.morovez.currencyconverter.data.currency_rate.repository.CurrencyRepositoryImpl
import com.morovez.currencyconverter.data.currency_rate.service.Api
import com.morovez.currencyconverter.data.currency_rate.service.NetworkService
import com.morovez.currencyconverter.data.currency_rate.service.NetworkServiceImpl
import com.morovez.currencyconverter.domain.account.repository_api.AccountRepository
import com.morovez.currencyconverter.domain.currency_rate.repository_api.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
interface DataModule{
    @Binds
    @Reusable
    fun bindNetworkService(impl: NetworkServiceImpl): NetworkService

    @Binds
    @Reusable
    fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton
    fun bindAccountStorage(impl: AccountStorageImpl): AccountStorage

    @Binds
    @Reusable
    fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}

@Module
class ApiModule {
    @Provides
    @Singleton
    fun provideNewsApiInstance(): Api {
        val interceptor = HttpLoggingInterceptor()

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(interceptor)
                    .build()
            )
            .build()
            .create(Api::class.java)
    }

    companion object{
        private const val BASE_URL = "https://www.cbr-xml-daily.ru/"
    }
}