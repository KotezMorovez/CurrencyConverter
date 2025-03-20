package com.morovez.currencyconverter.ui.adapters

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil

class CardDiffCallback: DiffUtil.ItemCallback<CardItemUI>(){
    override fun areItemsTheSame(oldItem: CardItemUI, newItem: CardItemUI): Boolean {
        return oldItem.currencyName == newItem.currencyName
    }

    override fun areContentsTheSame(oldItem: CardItemUI, newItem: CardItemUI): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: CardItemUI, newItem: CardItemUI): Any? {
        if (oldItem.currencyName == newItem.currencyName) {
            return if (oldItem.currentValue == newItem.currentValue) {
                super.getChangePayload(oldItem, newItem)
            } else {
                val diff = Bundle()
                diff.putString(ARG_VALUE, newItem.currentValue)
                diff
            }
        }

        return super.getChangePayload(oldItem, newItem)
    }
}