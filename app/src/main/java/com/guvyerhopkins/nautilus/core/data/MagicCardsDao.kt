package com.guvyerhopkins.nautilus.core.data

import androidx.room.*
import com.guvyerhopkins.nautilus.core.network.MagicCardsResponse

@Dao
interface MagicCardsDao {

    @Query("SELECT * FROM cards WHERE query = :query AND page = :page")
    suspend fun getCards(query: String, page: Int): MagicCardsResponse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardsResponse: MagicCardsResponse)

    @Delete
    fun delete(cardsResponse: MagicCardsResponse)
}