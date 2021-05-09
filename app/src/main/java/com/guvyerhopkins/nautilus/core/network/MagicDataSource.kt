package com.guvyerhopkins.nautilus.core.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.guvyerhopkins.nautilus.core.data.MagicCardsDao
import kotlinx.coroutines.*

class MagicDataSource(
    private val query: String,
    private val scope: CoroutineScope,
    cardsDao: MagicCardsDao
) :
    PageKeyedDataSource<Int, Card>() {

    private val repo = MagicRepository(MagicApi.retrofitSeservice, cardsDao)
    private val networkState = MutableLiveData<NetworkState>()
    private var supervisorJob = SupervisorJob()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Card>
    ) {
        if (query.isNotEmpty()) {//this prevents searching on initialization
            executeQuery(1, params.requestedLoadSize) {
                callback.onResult(it, null, 2)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Card>) {
        val page = params.key
        executeQuery(page, params.requestedLoadSize) {
            callback.onResult(it, page - 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Card>) {
        val page = params.key
        executeQuery(page, params.requestedLoadSize) {
            callback.onResult(it, page + 1)
        }
    }

    private fun executeQuery(page: Int, perPage: Int, callback: (List<Card>) -> Unit) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch(getJobErrorHandler() + supervisorJob) {
            delay(1000) // 1 second debounce
            val databaseCards = repo.getCardsFromDataBase(page, query)

            val cards = if (databaseCards.isNullOrEmpty()) {
                val networkCards = repo.getCards(page, perPage, query)
                if (networkCards.isNotEmpty()) {
                    Log.d("NautilusLog", "Inserting into database")
                    repo.insertIntoDataBase(networkCards, query, page)
                }
                Log.d("NautilusLog", "Using network response")
                networkCards
            } else {
                Log.d("NautilusLog", "Using cached response")
                databaseCards
            }

            if (cards.isEmpty()) {
                networkState.postValue(NetworkState.ZERORESULTS)
            }

            networkState.postValue(NetworkState.SUCCESS)
            callback(cards)
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(MagicCardsResponse::class.java.simpleName, "An error happened: $e")
        networkState.postValue(NetworkState.ERROR)
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }

    fun refresh() = this.invalidate()

    fun getNetworkState(): LiveData<NetworkState> =
        networkState
}