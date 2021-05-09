package com.guvyerhopkins.nautilus.ui.search

import com.guvyerhopkins.nautilus.core.data.MagicCardsDao
import com.guvyerhopkins.nautilus.core.network.MagicDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel

    private val dataSourceFactory = mock(MagicDataSourceFactory::class.java)

    @Before
    fun setUp() {
        viewModel = SearchViewModel(
            mock(MagicCardsDao::class.java),
            CoroutineScope(Dispatchers.Main),
            dataSourceFactory
        )
    }

    @Test
    fun onSearch_doesNotUpdateQuery() {
        `when`(dataSourceFactory.query).thenReturn("test")
        viewModel.search("test")
        verify(dataSourceFactory, times(0)).updateQuery(anyString())
    }

    @Test
    fun onSearch_updateQuery() {
        `when`(dataSourceFactory.query).thenReturn("test")
        viewModel.search("testt")
        verify(dataSourceFactory, times(1)).updateQuery(anyString())
    }
}