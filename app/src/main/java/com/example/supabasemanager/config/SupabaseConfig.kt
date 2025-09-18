package com.example.supabasemanager.config

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseConfig {
    // Supabase project credentials for Android Activity Manager
    private const val SUPABASE_URL = "https://rzgfrbddkcgqddmijzxz.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ6Z2ZyYmRka2NncWRkbWlqenh6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTgxNTc0MzUsImV4cCI6MjA3MzczMzQzNX0.DhntXoAv0VXxOda4PAloewnsXi3XEF9Gt8cCnEye0cY"
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(GoTrue)
        install(Storage)
        install(Realtime)
    }
}