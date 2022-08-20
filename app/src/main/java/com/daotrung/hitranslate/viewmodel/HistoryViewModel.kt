package com.daotrung.hitranslate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.model.TranslateHistory
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HistoryViewModel : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val liveDataHistory: MutableLiveData<List<TranslateHistory>> by lazy {
        MutableLiveData<List<TranslateHistory>>()
    }

    fun getHistory(): LiveData<PagedList<TranslateHistory>> {
        val dataSource = App.getDb().appDao().getFavoritePage(false)
        return LivePagedListBuilder<Int, TranslateHistory>(
            dataSource,
            PagedList
                .Config
                .Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .build()
        ).build()
    }

    fun getFavoritePage(): LiveData<PagedList<TranslateHistory>> {
        val dataSource = App.getDb().appDao().getFavoritePage(true)
        return LivePagedListBuilder<Int, TranslateHistory>(
            dataSource,
            PagedList
                .Config
                .Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .build()
        ).build()
    }

    fun loadData(){
        launch(Dispatchers.Main){
            val dataSource = App.getDb().appDao().delete30Days(false)
            liveDataHistory.value = withContext(Dispatchers.IO){
                dataSource
            }
        }
    }
    fun getHistory30Days(): MutableLiveData<List<TranslateHistory>> {
        return liveDataHistory
    }
}