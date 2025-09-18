# Supabase Manager Android App

A comprehensive Android application that connects to Supabase for remote data management and activity tracking.

## Features

- **Authentication**: Sign up, sign in, and sign out with Supabase Auth
- **Activity Management**: Create, read, update, and delete activities
- **Real-time Sync**: All data is synchronized with Supabase in real-time
- **Modern UI**: Material Design 3 components with beautiful UI
- **Offline Support**: Local data caching with automatic sync

## Prerequisites

1. **Android Studio**: Latest version with Kotlin support
2. **Supabase Account**: Create a free account at [supabase.com](https://supabase.com)
3. **Android SDK**: API level 24 or higher

## Supabase Setup

### 1. Create a Supabase Project

1. Go to [supabase.com](https://supabase.com) and sign up/sign in
2. Click "New Project"
3. Fill in your project details
4. Wait for the project to be created

### 2. Get Your Project Credentials

1. Go to your project dashboard
2. Click on "Settings" in the sidebar
3. Click on "API" under the Project Settings
4. Copy your:
   - **Project URL**
   - **Anon Public Key**

### 3. Create Database Tables

Run the following SQL in your Supabase SQL Editor:

```sql
-- Create profiles table
CREATE TABLE public.profiles (
    id UUID REFERENCES auth.users ON DELETE CASCADE,
    email TEXT,
    full_name TEXT,
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (id)
);

-- Create categories table
CREATE TABLE public.categories (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    color TEXT DEFAULT '#6366f1',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE
);

-- Create activities table
CREATE TABLE public.activities (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT DEFAULT 'General',
    status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    priority TEXT DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    due_date TIMESTAMP WITH TIME ZONE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE
);

-- Enable Row Level Security (RLS)
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.activities ENABLE ROW LEVEL SECURITY;

-- Create policies for profiles
CREATE POLICY "Public profiles are viewable by everyone." ON public.profiles
    FOR SELECT USING (true);

CREATE POLICY "Users can insert their own profile." ON public.profiles
    FOR INSERT WITH CHECK (auth.uid() = id);

CREATE POLICY "Users can update own profile." ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

-- Create policies for categories
CREATE POLICY "Users can view own categories." ON public.categories
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own categories." ON public.categories
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own categories." ON public.categories
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own categories." ON public.categories
    FOR DELETE USING (auth.uid() = user_id);

-- Create policies for activities
CREATE POLICY "Users can view own activities." ON public.activities
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own activities." ON public.activities
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own activities." ON public.activities
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own activities." ON public.activities
    FOR DELETE USING (auth.uid() = user_id);

-- Create function to handle new user profiles
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, full_name)
    VALUES (NEW.id, NEW.email, NEW.raw_user_meta_data->>'full_name');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Create trigger for new user profiles
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();
```

## App Configuration

### 1. Update Supabase Configuration

1. Open `app/src/main/java/com/example/supabasemanager/config/SupabaseConfig.kt`
2. Replace the placeholder values:

```kotlin
private const val SUPABASE_URL = "YOUR_PROJECT_URL_HERE"
private const val SUPABASE_ANON_KEY = "YOUR_ANON_KEY_HERE"
```

### 2. Build and Run

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build and run the app on your device or emulator

## Usage

### Authentication
1. Open the app
2. Enter your email and password
3. Tap "Sign Up" to create a new account or "Sign In" to log in
4. Check your email for verification (if signing up)

### Managing Activities
1. After signing in, you'll see the activities section
2. Tap the "+" button to add a new activity
3. Fill in the title, description, category, and priority
4. Tap "Save" to create the activity
5. Tap the delete icon on any activity to remove it

## Project Structure

```
app/
├── src/main/java/com/example/supabasemanager/
│   ├── adapter/           # RecyclerView adapters
│   ├── config/            # Supabase configuration
│   ├── models/            # Data models
│   ├── repository/        # Data repository layer
│   ├── viewmodel/         # ViewModels for MVVM
│   └── MainActivity.kt    # Main activity
├── res/
│   ├── layout/            # XML layouts
│   ├── values/            # Colors, strings, themes
│   └── drawable/          # Drawable resources
└── AndroidManifest.xml    # App manifest
```

## Dependencies

- **Supabase Kotlin SDK**: For database and authentication
- **Material Components**: For modern UI design
- **Kotlin Coroutines**: For asynchronous operations
- **AndroidX Lifecycle**: For ViewModels and LiveData
- **RecyclerView**: For efficient list display

## Troubleshooting

### Common Issues

1. **Build Errors**: Make sure you have the latest Android Studio and Gradle
2. **Network Errors**: Check your internet connection and Supabase credentials
3. **Authentication Fails**: Verify your Supabase project settings and RLS policies
4. **Data Not Showing**: Check that RLS policies are properly configured

### Debugging

1. Check Logcat for error messages
2. Verify Supabase credentials in `SupabaseConfig.kt`
3. Test API calls in Supabase dashboard
4. Check network permissions in AndroidManifest.xml

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is open source and available under the MIT License.