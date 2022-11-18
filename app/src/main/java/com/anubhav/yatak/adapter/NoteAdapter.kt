package com.anubhav.yatak.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anubhav.yatak.Util
import com.anubhav.yatak.databinding.NoteLayoutAdapterBinding
import com.anubhav.yatak.fragments.HomeFragmentDirections
import com.anubhav.yatak.model.Note
import java.math.BigInteger
import java.security.MessageDigest

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteLayoutAdapterBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.noteBody == newItem.noteBody &&
                        oldItem.noteTitle == newItem.noteTitle
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.tvNoteTitle.text = currentNote.noteTitle + ".txt"
        holder.itemBinding.tvNoteBody.text = currentNote.noteBody.toString()

        holder.itemView.setOnClickListener {

            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Enter Password")
            builder.setMessage("Enter password to open the note")

            val input = EditText(it.context)
            input.transformationMethod = PasswordTransformationMethod.getInstance()
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val password = input.text.toString().sha256()
                val savedPassword = Util.password

                if (savedPassword == password) {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)
                    it.findNavController().navigate(action)
                } else {
                    Toast.makeText(it.context, "Wrong Password", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setCancelable(true)
            builder.show()
        }
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
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}