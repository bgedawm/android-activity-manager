package com.example.supabasemanager.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Activity(
    val id: String? = null,
    val title: String,
    val description: String,
    val category: String,
    val status: ActivityStatus = ActivityStatus.PENDING,
    val priority: Priority = Priority.MEDIUM,
    val created_at: String? = null,
    val updated_at: String? = null,
    val due_date: String? = null,
    val user_id: String? = null
)

@Serializable
enum class ActivityStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

@Serializable
enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

@Serializable
data class Category(
    val id: String? = null,
    val name: String,
    val color: String = "#6366f1",
    val created_at: String? = null,
    val user_id: String? = null
)

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)