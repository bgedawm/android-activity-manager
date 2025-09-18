package com.example.supabasemanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.supabasemanager.adapter.ActivityAdapter
import com.example.supabasemanager.databinding.ActivityMainBinding
import com.example.supabasemanager.databinding.DialogAddActivityBinding
import com.example.supabasemanager.models.Priority
import com.example.supabasemanager.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var activityAdapter: ActivityAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        activityAdapter = ActivityAdapter { activity ->
            // Handle delete activity
            AlertDialog.Builder(this)
                .setTitle("Delete Activity")
                .setMessage("Are you sure you want to delete \"${activity.title}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteActivity(activity)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.activitiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = activityAdapter
        }
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.signIn(email, password)
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.signUp(email, password)
        }

        binding.signOutButton.setOnClickListener {
            viewModel.signOut()
        }

        binding.addActivityFab.setOnClickListener {
            showAddActivityDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                binding.authCard.visibility = View.GONE
                binding.activitiesCard.visibility = View.VISIBLE
                binding.signOutButton.visibility = View.VISIBLE
            } else {
                binding.authCard.visibility = View.VISIBLE
                binding.activitiesCard.visibility = View.GONE
                binding.signOutButton.visibility = View.GONE
                // Clear form fields
                binding.emailEditText.text?.clear()
                binding.passwordEditText.text?.clear()
            }
        }

        viewModel.activities.observe(this) { activities ->
            activityAdapter.submitList(activities)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // You can show/hide a progress bar here if needed
            binding.signInButton.isEnabled = !isLoading
            binding.signUpButton.isEnabled = !isLoading
            binding.signOutButton.isEnabled = !isLoading
            binding.addActivityFab.isEnabled = !isLoading
        }

        viewModel.statusMessage.observe(this) { message ->
            binding.statusText.text = message
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun showAddActivityDialog() {
        val dialogBinding = DialogAddActivityBinding.inflate(layoutInflater)
        
        // Setup priority spinner
        val priorityOptions = Priority.values().map { it.name }
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorityOptions)
        dialogBinding.prioritySpinner.setAdapter(priorityAdapter)
        dialogBinding.prioritySpinner.setText(Priority.MEDIUM.name, false)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.saveButton.setOnClickListener {
            val title = dialogBinding.titleEditText.text.toString()
            val description = dialogBinding.descriptionEditText.text.toString()
            val category = dialogBinding.categoryEditText.text.toString()
            val priorityText = dialogBinding.prioritySpinner.text.toString()
            
            val priority = try {
                Priority.valueOf(priorityText)
            } catch (e: IllegalArgumentException) {
                Priority.MEDIUM
            }

            if (title.isNotBlank()) {
                viewModel.createActivity(
                    title = title,
                    description = description,
                    category = category.ifBlank { "General" },
                    priority = priority
                )
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}