package com.guvyerhopkins.nautilus.data

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.guvyerhopkins.nautilus.network.MagicCardsResponse

@Dao
interface MagicCardsDao {

    @RawQuery(observedEntities = [MagicCardsResponse::class])
    suspend fun getCards(query: SimpleSQLiteQuery): MagicCardsResponse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardsResponse: MagicCardsResponse)//todo need to replace by query and page values

    @Delete
    fun delete(cardsResponse: MagicCardsResponse)
}

class RawQueries {
    companion object {
        fun getCardsQuery(query: String, page: Int): SimpleSQLiteQuery { //todo this is not working
            return SimpleSQLiteQuery("SELECT * FROM cards WHERE query = :$query AND page = :$page")
        }
    }
}