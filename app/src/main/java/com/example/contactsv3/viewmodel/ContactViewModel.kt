package com.example.contactsv3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsv3.data.ContactRepository
import com.example.contactsv3.model.Contact

class ContactViewModel : ViewModel() {
    private val _contacts = MutableLiveData<List<Contact>>(ContactRepository.getContacts())
    val contacts: LiveData<List<Contact>> get() = _contacts

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery

    fun refreshContacts() {
        _contacts.value = ContactRepository.getContacts()
    }

    fun getContactById(id: Long): Contact? {
        return ContactRepository.getContactById(id)
    }

    fun updateContact(contact: Contact) {
        ContactRepository.updateContact(contact)
        refreshContacts()
    }

    fun addContact(contact: Contact) {
        ContactRepository.addContact(contact)
        refreshContacts()
    }

    fun deleteContact(id: Long) {
        ContactRepository.deleteContact(id)
        refreshContacts()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredContacts(query: String): List<Contact> {
        val currentList = ContactRepository.getContacts()
        if (query.isBlank()) return currentList
        val lowerQuery = query.lowercase().trim()
        return currentList.filter { contact ->
            contact.nom.lowercase().contains(lowerQuery) ||
            contact.prenom.lowercase().contains(lowerQuery) ||
            contact.telephone.contains(lowerQuery)
        }
    }
}
