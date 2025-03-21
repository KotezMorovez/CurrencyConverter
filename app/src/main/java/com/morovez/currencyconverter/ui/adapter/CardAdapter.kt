package com.morovez.currencyconverter.ui.adapter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.morovez.currencyconverter.databinding.ItemCurrencyCardBinding
import com.morovez.currencyconverter.ui.model.CardItemUI

const val ARG_VALUE = "value"

class CardAdapter(
    private val onValueChangelistener: (String) -> Unit
) : ListAdapter<CardItemUI, CardAdapter.ViewHolder>(CardDiffCallback()) {
    private val textWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                onValueChangelistener.invoke(s?.ifEmpty { 0f }.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCurrencyCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    private fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Bundle>) {
        val item = getItem(position)

        if (payload.isEmpty()) {
            holder.bind(item)
        } else {
            holder.update(payload[0])
        }
    }

    inner class ViewHolder(val binding: ItemCurrencyCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardItemUI) {
            with(binding) {
                currencyNameTextView.text = item.currencyName
                walletTextView.text = item.walletTotal
                symbolTextView.text = item.symbol
                converterValueTextView.text = item.relevantExchangeRate

                valueToConvertEditText.removeTextChangedListener(textWatcher)
                valueToConvertEditText.setText(item.currentValue)
                valueToConvertEditText.setSelection(item.currentValue.length)
                valueToConvertEditText.addTextChangedListener(textWatcher)
            }
        }

        fun update(bundle: Bundle) {
            if (bundle.containsKey(ARG_VALUE)) {
                val newValue = bundle.getString(ARG_VALUE)

                with(binding) {
                    valueToConvertEditText.removeTextChangedListener(textWatcher)
                    valueToConvertEditText.setText(newValue)

                    newValue?.let { valueToConvertEditText.setSelection(it.length) }

                    valueToConvertEditText.addTextChangedListener(textWatcher)
                }
            }
        }

    }
}