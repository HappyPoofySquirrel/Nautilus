package com.guvyerhopkins.nautilus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.guvyerhopkins.nautilus.network.CardTypeConverter
import com.guvyerhopkins.nautilus.network.MagicCardsResponse

@TypeConverters(CardTypeConverter::class)
@Database(entities = [MagicCardsResponse::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardsDao(): MagicCardsDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "NautilusDatabase"
            )
                .build() // .allowMainThreadQueries()
        }
    }
}