package com.anubhav.yatak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.anubhav.yatak.databinding.ActivityAuthBinding
import com.anubhav.yatak.repository.NoteRepository
import com.anubhav.yatak.viewmodel.NoteViewModel
import com.anubhav.yatak.viewmodel.NoteViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var navController : NavController
    lateinit var noteViewModel: NoteViewModel
    private val noteRepository: NoteRepository = NoteRepository(
        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().uid.toString()))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        navController = binding.mainFragmentContainer.getFragment<NavHostFragment>().navController


        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){

                        val intent = Intent(this@AuthActivity, PasswordActivity::class.java)
                        startActivity(intent)
                        finish()

            }
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