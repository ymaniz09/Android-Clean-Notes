package com.github.ymaniz09.cleannotes.framework.presentation.notedetail

import android.os.Bundle
import android.view.View
import com.github.ymaniz09.cleannotes.R
import com.github.ymaniz09.cleannotes.framework.presentation.common.BaseNoteFragment

const val NOTE_DETAIL_STATE_BUNDLE_KEY = "com.codingwithmitch.cleannotes.notes.framework.presentation.notedetail.state"

class NoteDetailFragment : BaseNoteFragment(R.layout.fragment_note_detail) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun inject() {
        TODO("prepare dagger")
    }


}