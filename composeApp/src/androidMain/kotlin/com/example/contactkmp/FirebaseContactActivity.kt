package com.example.contactkmp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class Contact(
    val id: String = "",
    val name: String = "",
    val number: String = ""
)

val PrimaryColor = Color(0xFF4C99E7)
val SecondaryColor = Color(0xFF6A9FD3)
val CardBackground = Color(0xFFF3EDF7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactAppScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var showDialog by remember { mutableStateOf(false) }
    var editContactId by remember { mutableStateOf<String?>(null) }

    val contacts = remember { mutableStateListOf<Contact>() }

    LaunchedEffect(true) {
        db.collection("contacts").addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore", "Listen failed", error)
                return@addSnapshotListener
            }
            contacts.clear()
            for (doc in value!!) {
                contacts.add(
                    Contact(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        number = doc.getString("number") ?: ""
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = { ModernTopBar() },
        floatingActionButton = {
            ModernFAB {
                editContactId = null
                showDialog = true
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(contacts) { contact ->
                ContactItem(
                    contact = contact,
                    onEdit = {
                        editContactId = contact.id
                        showDialog = true
                    },
                    onDelete = {
                        db.collection("contacts").document(contact.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
            }
        }

        if (showDialog) {
            AddEditContactDialog(
                isEdit = editContactId != null,
                contact = contacts.find { it.id == editContactId },
                onDismiss = { showDialog = false },
                onSave = { name, number ->
                    if (editContactId == null) {
                        db.collection("contacts").add(
                            mapOf(
                                "name" to name,
                                "number" to number
                            )
                        )
                    } else {
                        // UPDATE CONTACT
                        db.collection("contacts").document(editContactId!!).update(
                            "name", name,
                            "number", number
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar() {
    TopAppBar(
        title = {
            Text(
                "Your Contacts",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryColor
        )
    )
}

@Composable
fun ModernFAB(onAdd: () -> Unit) {
    FloatingActionButton(
        onClick = onAdd,
        containerColor = PrimaryColor,
        contentColor = Color.White
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Contact")
    }
}

@Composable
fun ContactItem(contact: Contact, onEdit: () -> Unit, onDelete: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.number,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryColor
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Contact", tint = PrimaryColor)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Contact")
                }
            }
        }
    }
}

@Composable
fun AddEditContactDialog(
    isEdit: Boolean,
    contact: Contact?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(contact?.name ?: "") }
    var number by remember { mutableStateOf(contact?.number ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEdit) "Edit Contact" else "Add Contact",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    shape = RoundedCornerShape(12.dp),
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = number,
                    shape = RoundedCornerShape(12.dp),
                    onValueChange = { number = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, number) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(if (isEdit) "Update" else "Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}