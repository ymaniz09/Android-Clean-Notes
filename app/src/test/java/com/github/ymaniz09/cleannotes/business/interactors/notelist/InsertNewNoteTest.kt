package com.github.ymaniz09.cleannotes.business.interactors.notelist

import com.github.ymaniz09.cleannotes.business.data.cache.CacheErrors
import com.github.ymaniz09.cleannotes.business.data.cache.FORCE_GENERAL_FAILURE
import com.github.ymaniz09.cleannotes.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.github.ymaniz09.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.github.ymaniz09.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.github.ymaniz09.cleannotes.business.domain.model.Note
import com.github.ymaniz09.cleannotes.business.domain.model.NoteFactory
import com.github.ymaniz09.cleannotes.business.domain.state.DataState
import com.github.ymaniz09.cleannotes.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_FAILED
import com.github.ymaniz09.cleannotes.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_SUCCESS
import com.github.ymaniz09.cleannotes.di.DependencyContainer
import com.github.ymaniz09.cleannotes.framework.presentation.notelist.state.NoteListStateEvent
import com.github.ymaniz09.cleannotes.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

@InternalCoroutinesApi
class InsertNewNoteTest {

    private val insertNewNote: InsertNewNote

    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        insertNewNote = InsertNewNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
            noteFactory = noteFactory
        )
    }

    @Test
    fun `Should update cache when a note is inserted successfully`() = runBlocking {
        val newNote = createNote()

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            body = newNote.body,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(newNote.title, newNote.body)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_SUCCESS
                )
            }
        })

        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertNoteEquals(newNote, cacheNoteThatWasInserted)
    }

    @Test
    fun `Should update network when a note is inserted successfully`() = runBlocking {
        val newNote = createNote()

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            body = newNote.body,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(newNote.title, newNote.body)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_SUCCESS
                )
            }
        })

        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertNoteEquals(newNote, networkNoteThatWasInserted)
    }

    @Test
    fun `Should not update network when a note is not inserted successfully`() = runBlocking {
        val newNote = createNote(noteId = FORCE_GENERAL_FAILURE)

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            body = newNote.body,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(newNote.title, newNote.body)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_FAILED
                )
            }
        })

        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertNoteEquals(null, networkNoteThatWasInserted)
    }

    @Test
    fun `Should not update cache when a note is not inserted successfully`() = runBlocking {
        val newNote = createNote(noteId = FORCE_GENERAL_FAILURE)

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            body = newNote.body,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(newNote.title, newNote.body)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_FAILED
                )
            }
        })

        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertNoteEquals(null, cacheNoteThatWasInserted)
    }

    @Test
    fun `Should not update network when an exception is thrown inserting a note`() = runBlocking {
        val newNote = createNote(noteId = FORCE_NEW_NOTE_EXCEPTION)

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            body = newNote.body,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(newNote.title, newNote.body)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message
                        ?.contains(CacheErrors.CACHE_ERROR_UNKNOWN) ?: false
                )
            }
        })

        val networkNoteThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertNoteEquals(null, networkNoteThatWasInserted)
    }

    @Test
    fun `Should not update cache when an exception is thrown inserting a note`() = runBlocking {
        val newNote = createNote(noteId = FORCE_NEW_NOTE_EXCEPTION)

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            body = newNote.body,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(newNote.title, newNote.body)
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message
                        ?.contains(CacheErrors.CACHE_ERROR_UNKNOWN) ?: false
                )
            }
        })

        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertNoteEquals(null, cacheNoteThatWasInserted)
    }

    private fun createNote(noteId: String = UUID.randomUUID().toString()): Note {
        return noteFactory.createSingleNote(
            id = noteId,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )
    }

    private fun assertNoteEquals(expected: Note?, actual: Note?) {
        assertEquals(expected?.id, actual?.id)
        assertEquals(expected?.title, actual?.title)
        assertEquals(expected?.body, actual?.body)
    }
}