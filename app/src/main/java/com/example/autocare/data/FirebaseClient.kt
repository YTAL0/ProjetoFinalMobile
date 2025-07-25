package com.example.autocare.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase


object FirebaseClient {

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    val firestore: FirebaseFirestore by lazy {
        val firestore = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        firestore.firestoreSettings = settings
        firestore
    }
}
