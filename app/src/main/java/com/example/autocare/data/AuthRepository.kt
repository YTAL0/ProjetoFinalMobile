package com.example.autocare.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.autocare.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseClient.auth
    private val firestore: FirebaseFirestore = FirebaseClient.firestore
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val TAG = "AuthRepository"

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            user?.let {
                val uid = it.uid
                val userRef = firestore.collection("usuarios").document(uid)
                val snapshot = userRef.get().await()

                if (!snapshot.exists()) {
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to it.displayName,
                        "email" to it.email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    userRef.set(userData).await()
                    Log.d(TAG, "Novo usuário do Google salvo no Firestore: $uid")
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro no login com Google: ${e.message}", e)
            false
        }
    }

    suspend fun uploadFileAndGetUrl(fileUri: Uri, folder: String): String? {
        val userId = getCurrentUserId() ?: return null
        return try {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("$folder/$userId/$fileName")
            storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao fazer upload do arquivo: ${e.message}", e)
            null
        }
    }

    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid
            if (uid != null) {
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("usuarios").document(uid).set(user).await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao registrar usuário: ${e.message}", e)
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro no login: ${e.message}")
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar e-mail de recuperação: ${e.message}")
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
