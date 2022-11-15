package com.anubhav.yatak.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.anubhav.yatak.*
import com.anubhav.yatak.databinding.FragmentNewNoteBinding
import com.anubhav.yatak.model.Note
import com.anubhav.yatak.helper.toast
import com.anubhav.yatak.repository.NoteRepository
import com.anubhav.yatak.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private val noteRepository: NoteRepository = NoteRepository()
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewNoteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).noteViewModel
        mView = view
    }

    private suspend fun saveNote(view: View) {
        val noteTitle = binding.etNoteTitle.text.toString().trim()

        if (noteTitle.isNotEmpty()) {

            val key = Util.password

            Log.d("Key",key)

            val encryptor = Encryptor()
            val tdes = TrippleDe()

            val data = binding.etNoteBody.text.trim().toString() + binding.etNoteBody.text.trim().toString().sha256()

            Log.d("Key", "saveNote: ${data + data.sha256() + " " + (data +  data.sha256()).length}")

            val noteData = encryptor.encrypt(requireContext(),data, key)

            val blowfishKnowledgeFactory = BlowfishKnowledgeFactory()

            val finalEncrypt = blowfishKnowledgeFactory.encrypt(noteData.noteBody, key)

            val finalfinalEncrypt = tdes._encrypt(finalEncrypt, key)

            Log.d("TAG", "encrypted: $finalEncrypt")

            val noteId = FirebaseDatabase.getInstance().getReference("notes").push().key.toString()
            val note = Note(noteId,noteTitle, finalfinalEncrypt, noteData.noteIV)

            noteViewModel.addNote(note)
            Snackbar.make(
                view, "File saved successfully",
                Snackbar.LENGTH_SHORT
            ).show()
            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
        } else {
            activity?.toast("Please enter file name")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                lifecycleScope.launch(Dispatchers.Main){
                    saveNote(mView)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val b = BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
        var fb = ""
        for(i in 0 until 64-b.length){
            fb += "0"
        }
        return fb+b
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}