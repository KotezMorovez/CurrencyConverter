package com.morovez.currencyconverter.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(
    private val dp: Int = 16
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val spaceInPx = (dp * view.context.resources.displayMetrics.density).toInt()

        outRect.left = spaceInPx
        outRect.right = spaceInPx
        outRect.bottom = 0
        outRect.top = 0
    }
}