package com.guvyerhopkins.nautilus.network

class MagicRepository(private val service: MagicApiService) {

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
}