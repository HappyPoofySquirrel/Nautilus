package com.guvyerhopkins.nautilus.core.network

import com.guvyerhopkins.nautilus.core.data.MagicCardsDao
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

        val databaseCards = cardsDao.getCards(query, page)
        return if (databaseCards != null && isWithinAppropriateTime(databaseCards)) {
            databaseCards.cards
        } else {
            listOf()
        }
    }

    private fun isWithinAppropriateTime(databaseCards: MagicCardsResponse): Boolean {
        val date = Date(databaseCards.createdTime)
        val day = 24 * 60 * 60 * 1000
        val isWithinTimeLimit = date.time > System.currentTimeMillis() - day
        if (!isWithinTimeLimit) {
            cardsDao.delete(databaseCards)
        }
        return isWithinTimeLimit
    }

    fun insertIntoDataBase(cards: List<Card>, query: String, page: Int) {
        cardsDao.insert(MagicCardsResponse(query = query, page = page, cards = cards))
    }
}