package com.daotrung.hitranslate.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.daotrung.hitranslate.database.Converters
import java.io.Serializable
import java.util.*

@Entity
data class TranslateHistory(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var idFavorite: Int,
    val imgFlagIn: Int,
    val txtIn: String,
    val imgFlagOut: Int,
    val txtOut: String,
    val toLanguage: String,
    val fromLanguage: String,
    var isFavorite: Boolean = false,
    @TypeConverters(Converters::class)
    var dateHistory: Date = Date()
) : Serializable