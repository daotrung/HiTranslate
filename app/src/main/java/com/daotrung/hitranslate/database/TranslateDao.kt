package com.daotrung.hitranslate.database

import androidx.paging.DataSource
import androidx.room.*
import com.daotrung.hitranslate.model.TranslateHistory

@Dao
interface TranslateDao {
    @Query("SELECT * FROM TranslateHistory ORDER BY idFavorite DESC LIMIT 100")
    fun getData(): List<TranslateHistory>

    @Query("SELECT * FROM TranslateHistory ORDER BY idFavorite DESC LIMIT 100")
    fun getDataPage(): DataSource.Factory<Int, TranslateHistory>

    @Query("SELECT * FROM TranslateHistory WHERE isFavorite = :isFavorite ORDER BY idFavorite DESC")
    fun getFavorite(isFavorite: Boolean): MutableList<TranslateHistory>

    @Query("SELECT * FROM TranslateHistory WHERE isFavorite = :isFavorite ORDER BY idFavorite DESC")
    fun getFavoritePage(isFavorite: Boolean): DataSource.Factory<Int, TranslateHistory>

    @Query("UPDATE TranslateHistory SET isFavorite =:isFavorite WHERE txtOut =:txtOut and txtIn =:txtInt")
    fun updateFavorite(isFavorite: Boolean, txtOut: String, txtInt: String)

    @Query("SELECT EXISTS(SELECT * FROM TranslateHistory WHERE txtOut =:txtOut and txtIn =:txtInt)")
    fun isExistTranslate(txtOut: String, txtInt: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTranslate(model: TranslateHistory)

    @Delete
    fun deleteHistory(item: TranslateHistory)

    @Query("select * from TranslateHistory where isFavorite = :isFavorite and dateHistory <= (1000 * strftime('%s', datetime('now', '-30 day')))")
    fun delete30Days(isFavorite: Boolean): List<TranslateHistory>
}