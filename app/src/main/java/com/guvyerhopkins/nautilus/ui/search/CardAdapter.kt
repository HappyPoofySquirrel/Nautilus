package com.guvyerhopkins.nautilus.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.guvyerhopkins.nautilus.R
import com.guvyerhopkins.nautilus.network.Card

class CardAdapter(private val onCardPressed: (String, ImageView) -> Unit) :
    PagedListAdapter<Card, CardAdapter.CardGridViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardAdapter.CardGridViewHolder {
        return CardGridViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_item, parent, false)
        )
    }

    override fun onBindViewHolder(holderGrid: CardGridViewHolder, position: Int) {
        val card = getItem(position)
        if (card != null) {
            holderGrid.bind(card)
        }
    }

    inner class CardGridViewHolder(
        private var view:
        View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(card: Card) {
            val imageView = view.findViewById<ImageView>(R.id.card_item_iv)
            imageView.load(card.imageUrl) {
                scale(Scale.FILL)
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
            }

            view.setOnClickListener { onCardPressed.invoke(card.imageUrl, imageView) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }
    }
}