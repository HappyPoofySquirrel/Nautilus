package com.guvyerhopkins.nautilus.network

import android.util.Log
import com.guvyerhopkins.nautilus.data.MagicCardsDao
import com.guvyerhopkins.nautilus.data.RawQueries
import java.util.*

class MagicRepository(private val service: MagicApiService, private val cardsDao: MagicCardsDao) {

    private suspend fun search(page: Int, perPage: Int, query: String) =
        service.getCards(query, page, perPage)

    suspend fun getCards(
        page: Int,
        perPage: Int,
        query: String
    ): List<Card> {
        if (query.isEmpty()) return listOf()
        val request = search(page, perPage, query)
        return request.cards
    }

    suspend fun getCardsFromDataBase(
        page: Int,
        query: String
    ): List<Card> {
        if (query.isEmpty()) return listOf()

        val databaseCards = cardsDao.getCards(RawQueries.getCardsQuery(query, page))
        Log.d("GuyverLog", "database cards empty: ${databaseCards == null}")

        return if (databaseCards != null && isWithinAppropriateTime(databaseCards)) {
            Log.d("GuyverLog", "returning database cards from repo")
            databaseCards.cards
        } else {
            Log.d("GuyverLog", "returning empty list from database cards")
            listOf()
        }
    }

    private fun isWithinAppropriateTime(databaseCards: MagicCardsResponse): Boolean {
        val date = Date(databaseCards.createdTime)
        val day = 24 * 60 * 60 * 1000
        val isWithinTimeLimit = date.time < System.currentTimeMillis() - day
        if (!isWithinTimeLimit) {
            Log.d("GuyverLog", "deleting database cards")
            cardsDao.delete(databaseCards)
        }

        return date.time < System.currentTimeMillis() - day
    }

    fun insertIntoDataBase(cards: List<Card>, query: String, page: Int) {
        cardsDao.insert(MagicCardsResponse(query = query, page = page, cards = cards))
    }
}