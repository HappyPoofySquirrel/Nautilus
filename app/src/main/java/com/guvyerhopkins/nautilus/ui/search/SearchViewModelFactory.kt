package com.guvyerhopkins.nautilus.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.guvyerhopkins.nautilus.core.data.MagicCardsDao


class SearchViewModelFactory(private val cardsDao: MagicCardsDao) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(cardsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}