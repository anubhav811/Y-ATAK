package com.anubhav.yatak.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.anubhav.yatak.*
import com.anubhav.yatak.databinding.FragmentUpdateNoteBinding
import com.anubhav.yatak.model.Note
import com.anubhav.yatak.helper.toast
import com.anubhav.yatak.viewmodel.NoteViewModel


class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    // shared pref

    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUpdateNoteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        val encryptor = Encryptor()

        val blowfishKnowledgeFactory = BlowfishKnowledgeFactory()

        val key = Util.password

        val decryptFirst = blowfishKnowledgeFactory.decrypt(currentNote.noteBody, key)

        val dataToDecrypt = decryptFirst.map { it.toByte() }.toByteArray()

        val decryptedString = encryptor.decrypt(requireContext(), dataToDecrypt, key.toString(), currentNote.noteIV)
        binding.etNoteBodyUpdate.setText(decryptedString)
        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)

        binding.fabDone.setOnClickListener {
            val title = binding.etNoteTitleUpdate.text.toString().trim()


                if (title.isNotEmpty()) {
                    if (binding.etNoteBodyUpdate.toString() != currentNote.noteBody) {
                        val newEncryptNote = encryptor.encrypt(
                            requireContext(),
                            binding.etNoteBodyUpdate.text.toString(),
                            key
                        )

                        val blowfishKnowledgeFactory = BlowfishKnowledgeFactory()

                        val finalEncrypt = blowfishKnowledgeFactory.encrypt(newEncryptNote.noteBody, key)

                        val note = Note(currentNote.id, binding.etNoteTitleUpdate.text.toString(), finalEncrypt, newEncryptNote.noteIV)
                        noteViewModel.updateNote(note)
                    }else{
                        val note = Note(currentNote.id,binding.etNoteTitleUpdate.text.toString(), binding.etNoteBodyUpdate.text.toString(), currentNote.noteIV)
                        noteViewModel.updateNote(note)
                    }
                    view.findNavController()
                        .navigate(R.id.action_updateNoteFragment_to_homeFragment)
                } else {
                    activity?.toast("Enter a note title please")
                }

        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Are you sure you want to permanently delete this note?")
            setPositiveButton("DELETE") { _, _ ->
                noteViewModel.deleteNote(currentNote)
                view?.findNavController()?.navigate(
                    R.id.action_updateNoteFragment_to_homeFragment
                )

            }
            setNegativeButton("CANCEL", null)
        }.create().show()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteNote()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}