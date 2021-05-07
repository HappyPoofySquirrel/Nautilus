package com.guvyerhopkins.nautilus.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.guvyerhopkins.nautilus.data.MagicCardsDao
import kotlinx.coroutines.CoroutineScope

class MagicDataSourceFactory(
    var query: String = "",
    private val scope: CoroutineScope,
    private val cardsDao: MagicCardsDao
) : DataSource.Factory<Int, Card>() {

    val source = MutableLiveData<MagicDataSource>()

    override fun create(): DataSource<Int, Card> {
        val source = MagicDataSource(query, scope, cardsDao)
        this.source.postValue(source)
        return source
    }

    fun updateQuery(query: String) {
        this.query = query
        source.value?.refresh()
    }
}