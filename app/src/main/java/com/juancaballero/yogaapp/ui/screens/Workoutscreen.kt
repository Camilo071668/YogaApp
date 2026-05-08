package com.juancaballero.yogaapp.ui.screens

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

fun updateProgress(minutesToAdd: Int) {
    val db = Firebase.firestore
    val uid = Firebase.auth.currentUser?.uid

    if (uid != null) {
        val userRef = db.collection("users").document(uid)
        // Usamos increment para que Firebase sume automáticamente
        userRef.update(
            "totalMinutes", FieldValue.increment(minutesToAdd.toLong()),
            "routinesCompleted", FieldValue.increment(1)
        )
    }
}