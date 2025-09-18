package com.example.supabasemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabasemanager.models.Activity
import com.example.supabasemanager.models.ActivityStatus
import com.example.supabasemanager.models.Priority
import com.example.supabasemanager.repository.SupabaseRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>> = _activities

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        val currentUser = repository.getCurrentUser()
        _isAuthenticated.value = currentUser != null
        if (currentUser != null) {
            _statusMessage.value = "Signed in as ${currentUser.email}"
            loadActivities()
        } else {
            _statusMessage.value = "Ready to connect to Supabase"
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.signIn(email, password)
            _isLoading.value = false

            if (result.isSuccess) {
                _isAuthenticated.value = true
                _statusMessage.value = "Signed in successfully"
                loadActivities()
            } else {
                _errorMessage.value = "Sign in failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.signUp(email, password)
            _isLoading.value = false

            if (result.isSuccess) {
                _statusMessage.value = "Account created successfully. Please check your email for verification."
            } else {
                _errorMessage.value = "Sign up failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.signOut()
            _isLoading.value = false

            if (result.isSuccess) {
                _isAuthenticated.value = false
                _activities.value = emptyList()
                _statusMessage.value = "Signed out successfully"
            } else {
                _errorMessage.value = "Sign out failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun loadActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getActivities()
            _isLoading.value = false

            if (result.isSuccess) {
                _activities.value = result.getOrNull() ?: emptyList()
                _statusMessage.value = "Activities loaded: ${result.getOrNull()?.size ?: 0} items"
            } else {
                _errorMessage.value = "Failed to load activities: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun createActivity(
        title: String,
        description: String,
        category: String,
        priority: Priority
    ) {
        if (title.isBlank()) {
            _errorMessage.value = "Title is required"
            return
        }

        val activity = Activity(
            title = title,
            description = description,
            category = category,
            priority = priority,
            status = ActivityStatus.PENDING
        )

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createActivity(activity)
            _isLoading.value = false

            if (result.isSuccess) {
                _statusMessage.value = "Activity created successfully"
                loadActivities() // Refresh the list
            } else {
                _errorMessage.value = "Failed to create activity: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun deleteActivity(activity: Activity) {
        val activityId = activity.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteActivity(activityId)
            _isLoading.value = false

            if (result.isSuccess) {
                _statusMessage.value = "Activity deleted successfully"
                loadActivities() // Refresh the list
            } else {
                _errorMessage.value = "Failed to delete activity: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}