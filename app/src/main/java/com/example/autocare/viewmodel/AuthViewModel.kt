package com.example.autocare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return repository.getGoogleSignInClient(context)
    }

    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginWithGoogle(idToken)
            onResult(success)
        }
    }

    fun register(email: String, password: String, name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.registerUser(email, password, name)
            onResult(success)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginUser(email, password)
            onResult(success)
        }
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.resetPassword(email)
            onResult(success)
        }
    }

    fun logout() {
        repository.logout()
    }

    fun isUserLogged(): Boolean {
        return repository.isUserLogged()
    }
}
