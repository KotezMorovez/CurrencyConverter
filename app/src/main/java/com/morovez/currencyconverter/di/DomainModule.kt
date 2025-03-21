package com.morovez.currencyconverter.di

import com.morovez.currencyconverter.domain.account.interactor.AccountInteractor
import com.morovez.currencyconverter.domain.account.interactor.AccountInteractorImpl
import com.morovez.currencyconverter.domain.currency_rate.interactor.CurrencyInteractor
import com.morovez.currencyconverter.domain.currency_rate.interactor.CurrencyInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface DomainModule {
    @Binds
    @Reusable
    fun bindCurrencyInteractor(impl: CurrencyInteractorImpl): CurrencyInteractor

    @Binds
    @Reusable
    fun bindAccountInteractor(impl: AccountInteractorImpl): AccountInteractor
}