package com.guvyerhopkins.nautilus.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

class MagicDataSourceFactory(
    var query: String = "",
    private val scope: CoroutineScope
) : DataSource.Factory<Int, Card>() {

    val source = MutableLiveData<MagicDataSource>()

    override fun create(): DataSource<Int, Card> {
        val source = MagicDataSource(query, scope)
        this.source.postValue(source)
        return source
    }

    fun updateQuery(query: String) {
        this.query = query
        source.value?.refresh()
    }
}