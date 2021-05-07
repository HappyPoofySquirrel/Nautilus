package com.guvyerhopkins.nautilus.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.guvyerhopkins.nautilus.data.MagicCardsDao
import kotlinx.coroutines.*

class MagicDataSource(
    private val query: String,
    private val scope: CoroutineScope,
    cardsDao: MagicCardsDao
) :
    PageKeyedDataSource<Int, Card>() {

    private val repo = MagicRepository(MagicApi.retrofitSeservice, cardsDao)
    private val networkState = MutableLiveData<State>()
    private var supervisorJob = SupervisorJob()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Card>
    ) {
        executeQuery(1, params.requestedLoadSize) {
            callback.onResult(it, null, 2)
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
        networkState.postValue(State.LOADING)
        scope.launch(getJobErrorHandler() + supervisorJob) {
            delay(1000) // 1 second debounce

//            withContext(scope.coroutineContext) {
            val databaseCards = repo.getCardsFromDataBase(page, query)

            Log.d("GuyverLog", "hit after getting databasecardsx")

            val cards = if (databaseCards.isNullOrEmpty()) {

//                val networkCards = withContext(scope.coroutineContext) {
                val networkCards = repo.getCards(page, perPage, query)
//                }
                Log.d("GuyverLog", "hit after calling network")

                if (networkCards.isNotEmpty()) {
                    Log.d("GuyverLog", "insert into database")
                    repo.insertIntoDataBase(networkCards, query, page)
                }
                Log.d("GuyverLog", "returning network cards. Count: ${networkCards.size}")
                networkCards
            } else {
                Log.d("GuyverLog", "returning database cards")
                databaseCards
            }

//            val cards =                     repo.getCards(page, perPage, query)

            Log.d("GuyverLog", "hitting callback")
            networkState.postValue(State.SUCCESS)
            callback(cards)
//            }
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(MagicCardsResponse::class.java.simpleName, "An error happened: $e")
        networkState.postValue(State.ERROR)
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }

    fun refresh() = this.invalidate()

    fun getNetworkState(): LiveData<State> =
        networkState
}