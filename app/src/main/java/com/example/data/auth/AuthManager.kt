package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AuthProvider {
    GUEST, GOOGLE, EMAIL
}

data class AuthState(
    val isAuthenticated: Boolean = true, // Default to true as Guest per prompt "Open -> Enter idea -> Generate"
    val provider: AuthProvider = AuthProvider.GUEST,
    val userEmail: String? = null,
    val userName: String = "VibeCreator",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("vibe_auth_prefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow(
        AuthState(
            isAuthenticated = prefs.getBoolean("is_authenticated", true),
            provider = AuthProvider.valueOf(prefs.getString("auth_provider", AuthProvider.GUEST.name) ?: AuthProvider.GUEST.name),
            userEmail = prefs.getString("user_email", null),
            userName = prefs.getString("user_name", "VibeCreator") ?: "VibeCreator"
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signInAsGuest() {
        prefs.edit()
            .putBoolean("is_authenticated", true)
            .putString("auth_provider", AuthProvider.GUEST.name)
            .putString("user_name", "VibeCreator")
            .remove("user_email")
            .apply()

        _authState.value = AuthState(
            isAuthenticated = true,
            provider = AuthProvider.GUEST,
            userName = "VibeCreator",
            userEmail = null
        )
    }

    fun signInWithGoogle(displayName: String = "Alex Rivera", email: String = "creator.alex@gmail.com") {
        prefs.edit()
            .putBoolean("is_authenticated", true)
            .putString("auth_provider", AuthProvider.GOOGLE.name)
            .putString("user_name", displayName)
            .putString("user_email", email)
            .apply()

        _authState.value = AuthState(
            isAuthenticated = true,
            provider = AuthProvider.GOOGLE,
            userName = displayName,
            userEmail = email
        )
    }

    fun signInWithEmail(email: String, name: String = "Alex") {
        val userName = if (name.isNotBlank()) name else email.substringBefore("@").replaceFirstChar { it.uppercase() }
        prefs.edit()
            .putBoolean("is_authenticated", true)
            .putString("auth_provider", AuthProvider.EMAIL.name)
            .putString("user_name", userName)
            .putString("user_email", email)
            .apply()

        _authState.value = AuthState(
            isAuthenticated = true,
            provider = AuthProvider.EMAIL,
            userName = userName,
            userEmail = email
        )
    }

    fun signOut() {
        prefs.edit()
            .putBoolean("is_authenticated", false)
            .remove("auth_provider")
            .remove("user_email")
            .apply()

        _authState.value = AuthState(
            isAuthenticated = false,
            provider = AuthProvider.GUEST,
            userName = "Guest",
            userEmail = null
        )
    }
}
