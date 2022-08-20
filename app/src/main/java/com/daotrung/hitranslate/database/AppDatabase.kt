package com.daotrung.hitranslate.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.daotrung.hitranslate.model.TranslateHistory

@Database(entities = [TranslateHistory::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): TranslateDao
}