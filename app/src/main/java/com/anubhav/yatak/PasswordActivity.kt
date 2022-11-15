package com.anubhav.yatak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.anubhav.yatak.databinding.ActivityPasswordBinding
import com.anubhav.yatak.repository.NoteRepository
import com.anubhav.yatak.viewmodel.NoteViewModel
import com.anubhav.yatak.viewmodel.NoteViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.security.MessageDigest

class PasswordActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityPasswordBinding
    lateinit var noteViewModel: NoteViewModel
    private val noteRepository: NoteRepository = NoteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        var password = ""

        GlobalScope.launch(Dispatchers.IO) {
            password = noteRepository.getPassword()
        }
        if (password.isEmpty()) {
            binding.tilPw2.visibility = View.VISIBLE
            binding.confirmButton.setOnClickListener {
                val pw = binding.etPw.text.toString().sha256()
                val confirmPw = binding.etPw2.text.toString().sha256()
                if (pw.isEmpty() || confirmPw.isEmpty()) {
                    Snackbar.make(binding.root, "Please enter password", Snackbar.LENGTH_SHORT)
                        .show()
                } else if (pw != confirmPw) {
                    Snackbar.make(binding.root, "Passwords do not match", Snackbar.LENGTH_SHORT)
                        .show()
                }else{
                    GlobalScope.launch(Dispatchers.IO) {
                        noteRepository.setPassword(pw)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            binding.confirmButton.setOnClickListener {
                val pw = binding.etPw.text.toString().sha256()
                val confpw = binding.etPw2.text.toString().sha256()
                Log.d("Key1", "onCreate: ${pw + " " + pw.length}")
                if (pw == password && confpw == password) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
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

    private fun setUpViewModel() {
        val noteRepository = NoteRepository()

        val viewModelProviderFactory =
            NoteViewModelProviderFactory(
                application, noteRepository
            )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[NoteViewModel::class.java]
    }

}