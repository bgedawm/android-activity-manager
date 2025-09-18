package com.example.supabasemanager.repository

import android.util.Log
import com.example.supabasemanager.config.SupabaseConfig
import com.example.supabasemanager.models.Activity
import com.example.supabasemanager.models.Category
import com.example.supabasemanager.models.UserProfile
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseRepository {
    private val client = SupabaseConfig.client
    private val TAG = "SupabaseRepository"

    // Authentication
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up failed", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed", e)
            Result.failure(e)
        }
    }

    fun getCurrentUser() = client.auth.currentUserOrNull()

    // Activities CRUD Operations
    suspend fun getActivities(): Result<List<Activity>> {
        return try {
            val activities = client.from("activities")
                .select()
                .decodeList<Activity>()
            Result.success(activities)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch activities", e)
            Result.failure(e)
        }
    }

    suspend fun createActivity(activity: Activity): Result<Activity> {
        return try {
            val createdActivity = client.from("activities")
                .insert(activity)
                .decodeSingle<Activity>()
            Result.success(createdActivity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create activity", e)
            Result.failure(e)
        }
    }

    suspend fun updateActivity(id: String, activity: Activity): Result<Activity> {
        return try {
            val updatedActivity = client.from("activities")
                .update(activity) {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<Activity>()
            Result.success(updatedActivity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update activity", e)
            Result.failure(e)
        }
    }

    suspend fun deleteActivity(id: String): Result<Unit> {
        return try {
            client.from("activities")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete activity", e)
            Result.failure(e)
        }
    }

    // Categories CRUD Operations
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categories = client.from("categories")
                .select()
                .decodeList<Category>()
            Result.success(categories)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch categories", e)
            Result.failure(e)
        }
    }

    suspend fun createCategory(category: Category): Result<Category> {
        return try {
            val createdCategory = client.from("categories")
                .insert(category)
                .decodeSingle<Category>()
            Result.success(createdCategory)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create category", e)
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            client.from("categories")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete category", e)
            Result.failure(e)
        }
    }

    // Real-time subscriptions
    fun getActivitiesFlow(): Flow<List<Activity>> = flow {
        try {
            val activities = getActivities().getOrThrow()
            emit(activities)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to emit activities", e)
            emit(emptyList())
        }
    }

    // User Profile Operations
    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val profile = client.from("profiles")
                .select()
                .eq("id", userId)
                .decodeSingle<UserProfile>()
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user profile", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> {
        return try {
            val updatedProfile = client.from("profiles")
                .update(profile) {
                    filter {
                        eq("id", profile.id)
                    }
                }
                .decodeSingle<UserProfile>()
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user profile", e)
            Result.failure(e)
        }
    }
}