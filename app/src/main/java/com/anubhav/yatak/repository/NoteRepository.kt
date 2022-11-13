package com.anubhav.yatak.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anubhav.yatak.Util
import com.anubhav.yatak.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class NoteRepository(
    private val notesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().uid.toString())
) {
    fun getAllNotes(): LiveData<List<Note>?> {
        Log.d("NoteRepository", "getAllNotes: getting Notes")

        val notes = mutableListOf<Note>()
        val notesLiveData = MutableLiveData<List<Note>?>()
        notesRef.get().addOnSuccessListener {
            for (note in it.children) {
                if(note.value !is String){
                    val currnote = note.getValue(Note::class.java)
                    if (currnote != null) {
                        notes.add(currnote)
                    }
                }
            }
            notesLiveData.value = notes
        }.addOnFailureListener {
            notesLiveData.value = null
        }
        Log.d("NoteRepository", "getAllNotes: ${notes.toString()}")
        return notesLiveData
    }
    suspend fun insert(note: Note) {
        try {
            notesRef.child(note.id).setValue(note).await()
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun delete(note: Note) {
        try
        {
            notesRef.child(note.id).removeValue().await()
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun update(note: Note) {
        try {
            notesRef.child(note.id).setValue(note).await()
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun setPassword(pw:String){
        try{
            notesRef.child("password").setValue(pw).await()
        }catch (exception:java.lang.Exception){
            throw exception
        }
        Util.password  = pw
    }

    suspend fun getPassword():String{
        var pw = ""
        pw=notesRef.child("password").get().await().value.toString()
        Log.d("NoteRepository: " , pw)
        return pw
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

}