package com.github.ymaniz09.cleannotes.di

import com.github.ymaniz09.cleannotes.business.data.cache.FakeNoteCacheDataSourceImpl
import com.github.ymaniz09.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.github.ymaniz09.cleannotes.business.data.network.FakeNoteNetworkDataSourceImpl
import com.github.ymaniz09.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.github.ymaniz09.cleannotes.business.domain.model.NoteFactory
import com.github.ymaniz09.cleannotes.business.domain.util.DateUtil
import com.github.ymaniz09.cleannotes.util.isUnitTest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DependencyContainer {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
    val dateUtil = DateUtil(dateFormat)
    lateinit var noteNetworkDataSource: NoteNetworkDataSource
    lateinit var noteCacheDataSource: NoteCacheDataSource
    lateinit var noteFactory: NoteFactory

    init {
        isUnitTest = true // for Logger.kt
    }

    fun build() {
        noteFactory = NoteFactory(dateUtil)
        noteNetworkDataSource = FakeNoteNetworkDataSourceImpl(
            notesData = HashMap(),
            deletedNotesData = HashMap(),
            dateUtil = dateUtil
        )
        noteCacheDataSource = FakeNoteCacheDataSourceImpl(
            notesData = HashMap(),
            dateUtil = dateUtil
        )
    }
}