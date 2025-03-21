package com.morovez.currencyconverter.ui.common

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import javax.inject.Inject

interface ResourceProvider {

    fun getString(@StringRes res: Int, vararg args: Any): String

    fun getString(@StringRes res: Int): String

    fun getColor(@ColorRes res: Int): Int
}

class ResourceProviderImpl @Inject constructor(
    private val context: Context
) : ResourceProvider {

    override fun getString(@StringRes res: Int, vararg args: Any): String {
        return context.resources.getString(res, args)
    }

    override fun getString(@StringRes res: Int): String {
        return context.resources.getString(res)
    }

    override fun getColor(@ColorRes res: Int): Int {
        return context.getColor(res)
    }
}