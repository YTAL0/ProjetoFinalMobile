package com.example.autocare.data

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseClient.auth
    private val firestore: FirebaseFirestore = FirebaseClient.firestore
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val TAG = "AuthRepository"

    suspend fun uploadFileAndGetUrl(fileUri: Uri, folder: String): String? {
        val userId = getCurrentUserId() ?: return null
        return try {
            val fileName = UUID.randomUUID().toString()

            val storageRef = storage.reference.child("$folder/$userId/$fileName")

            storageRef.putFile(fileUri).await()
            Log.d(TAG, "Upload do arquivo bem-sucedido: ${storageRef.path}")

            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d(TAG, "URL de download obtida: $downloadUrl")
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
                Log.d(TAG, "Usuário registrado com sucesso: $uid")
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
            Log.d(TAG, "Login bem-sucedido para o e-mail: $email")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro no login: ${e.message}")
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "E-mail de redefinição de senha enviado para: $email")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar e-mail de recuperação: ${e.message}")
            false
        }
    }


    fun logout() {
        auth.signOut()
        Log.d(TAG, "Usuário deslogado.")
    }

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
