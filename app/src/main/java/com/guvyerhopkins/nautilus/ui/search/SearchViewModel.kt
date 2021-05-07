package com.guvyerhopkins.nautilus.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.guvyerhopkins.nautilus.data.MagicCardsDao
import com.guvyerhopkins.nautilus.network.MagicDataSourceFactory
import com.guvyerhopkins.nautilus.network.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel


class SearchViewModel(
    private val cardsDao: MagicCardsDao,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val dataSourceFactory: MagicDataSourceFactory = MagicDataSourceFactory(
        scope = ioScope,
        cardsDao = cardsDao
    ),
) : ViewModel() {

    val networkState: LiveData<State>? =
        switchMap(dataSourceFactory.source) { it.getNetworkState() }

    val cards = LivePagedListBuilder(
        dataSourceFactory, PagedList.Config.Builder()
            .setInitialLoadSizeHint(30)
            .setEnablePlaceholders(false)
            .setPageSize(30 * 2)
            .build()
    ).build()

    fun search(query: String) {
        if (query == dataSourceFactory.query) {
            return//return if query did not change
        }

        if (query.length > 2) {
            dataSourceFactory.updateQuery(query.trim())
        }
    }

    override fun onCleared() {
        super.onCleared()
        ioScope.coroutineContext.cancel()
    }
}