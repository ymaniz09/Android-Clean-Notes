package com.github.ymaniz09.cleannotes.business.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: String,
    val title: String,
    val body: String,
    val updated_at: String,
    val created_at: String
) : Parcelable