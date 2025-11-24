package com.example.contactkmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactkmp.Contact
import com.example.contactkmp.data.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactViewModel(
    private val repository: ContactRepository = ContactRepository()
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    init {
        repository.listenContacts { list ->
            _contacts.value = list
        }
    }

    fun addContact(name: String, number: String) {
        viewModelScope.launch {
            repository.addContact(name, number)
        }
    }

    fun updateContact(id: String, name: String, number: String) {
        viewModelScope.launch {
            repository.updateContact(id, name, number)
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            repository.deleteContact(id)
        }
    }
}
