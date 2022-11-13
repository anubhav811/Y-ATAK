package com.anubhav.yatak;

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.anubhav.yatak.databinding.ActivityMainBinding
import com.anubhav.yatak.repository.NoteRepository
import com.anubhav.yatak.viewmodel.NoteViewModel
import com.anubhav.yatak.viewmodel.NoteViewModelProviderFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpViewModel()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // create alert dialog
                val builder = AlertDialog.Builder(this)
                val dialog : AlertDialog = builder.create()
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to logout?")
                builder.setPositiveButton("Yes"){ _, _ ->
                    // logout
                    noteViewModel.logout()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
                builder.setNegativeButton("No"){ _, _ ->
                    // dismiss dialog
                    dialog.dismiss()
                }
                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)

    }

}