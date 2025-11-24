package com.example.contactkmp.data

import com.example.contactkmp.Contact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactRepository {
    private val db = FirebaseFirestore.getInstance()
    private val contactsRef = db.collection("contacts")

    // Add Contact
    suspend fun addContact(name: String, number: String) {
        val contact = mapOf(
            "name" to name,
            "number" to number
        )
        contactsRef.add(contact).await()
    }

    // Update Contact
    suspend fun updateContact(id: String, name: String, number: String) {
        contactsRef.document(id).update(
            "name", name,
            "number", number
        ).await()
    }

    // Delete Contact
    suspend fun deleteContact(id: String) {
        contactsRef.document(id).delete().await()
    }

    // Listen to contact changes (callback)
    fun listenContacts(onChange: (List<Contact>) -> Unit) {
        contactsRef.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            val list = snapshot.documents.map { doc ->
                Contact(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    number = doc.getString("number") ?: ""
                )
            }
            onChange(list)
        }
    }
}
