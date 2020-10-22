package com.github.ymaniz09.cleannotes.business.data.network

import com.github.ymaniz09.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.github.ymaniz09.cleannotes.business.domain.model.Note
import com.github.ymaniz09.cleannotes.business.domain.util.DateUtil

class FakeNoteNetworkDataSourceImpl
constructor(
    private val notesData: HashMap<String, Note>,
    private val deletedNotesData: HashMap<String, Note>,
    private val dateUtil: DateUtil
) : NoteNetworkDataSource {

    override suspend fun insertOrUpdateNote(note: Note) {
        val n = Note(
            id = note.id,
            title = note.title,
            body = note.body,
            created_at = note.created_at,
            updated_at = dateUtil.getCurrentTimestamp()
        )
        notesData[note.id] = n
    }

    override suspend fun deleteNote(primaryKey: String) {
        notesData.remove(primaryKey)
    }

    override suspend fun insertDeletedNote(note: Note) {
        deletedNotesData[note.id] = note
    }

    override suspend fun insertDeletedNotes(notes: List<Note>) {
        for (note in notes) {
            deletedNotesData[note.id] = note
        }
    }

    override suspend fun deleteDeletedNote(note: Note) {
        deletedNotesData.remove(note.id)
    }

    override suspend fun getDeletedNotes(): List<Note> {
        return ArrayList(deletedNotesData.values)
    }

    override suspend fun deleteAllNotes() {
        deletedNotesData.clear()
    }

    override suspend fun searchNote(note: Note): Note? {
        return notesData[note.id]
    }

    override suspend fun getAllNotes(): List<Note> {
        return ArrayList(notesData.values)
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
        for (note in notes) {
            notesData[note.id] = note
        }
    }
}