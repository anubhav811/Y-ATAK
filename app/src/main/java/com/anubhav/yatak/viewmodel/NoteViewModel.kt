package com.anubhav.yatak.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anubhav.yatak.model.Note
import com.anubhav.yatak.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    private val noteRepository: NoteRepository
) : AndroidViewModel(app) {

    fun addNote(note: Note) =
        viewModelScope.launch {
            noteRepository.insert(note)
        }

    fun deleteNote(note: Note) =
        viewModelScope.launch {
            noteRepository.delete(note)
        }

    fun updateNote(note: Note) =
        viewModelScope.launch {
            noteRepository.update(note)
        }

    fun getAllNotes() : LiveData<List<Note>?> = noteRepository.getAllNotes()

    fun setPassword(pw:String){
        viewModelScope.launch {
            noteRepository.setPassword(pw)
        }
    }
    fun logout(){
        viewModelScope.launch {
            noteRepository.logout()
        }
    }
}