package com.example.contactsv3.data

import com.example.contactsv3.model.Contact

object ContactRepository {
    private val contacts = mutableListOf(
        Contact(1L, "Dupont", "Jean", "0612345678", "jean.dupont@email.com", null, 36.8065, 10.1815), // Tunis
        Contact(2L, "Curie", "Marie", "0698765432", "marie.curie@email.com", null, 48.8584, 2.2945), // Paris
        Contact(3L, "Martin", "Paul", "0611223344", "paul.martin@email.com", null, 51.5074, -0.1278), // London
        Contact(4L, "Bernard", "Sophie", "0655443322", "sophie.bernard@email.com", null, 40.7128, -74.0060), // New York
        Contact(5L, "Petit", "Lucas", "0677889900", "lucas.petit@email.com", null, 35.6762, 139.6503), // Tokyo
        Contact(6L, "Rossi", "Marco", "0633221100", "marco.rossi@email.com", null, 41.9028, 12.4964), // Rome
        Contact(7L, "Weber", "Anna", "0644556677", "anna.weber@email.com", null, 52.5200, 13.4050), // Berlin
        Contact(8L, "Ndiaye", "Fatou", "0688990011", "fatou.ndiaye@email.com", null, null, null) // Sans position GPS
    )

    private var nextId = 9L

    fun getContacts(): List<Contact> = contacts.toList()

    fun getContactById(id: Long): Contact? = contacts.find { it.id == id }

    fun addContact(contact: Contact) {
        val newContact = contact.copy(id = nextId++)
        contacts.add(newContact)
    }

    fun updateContact(updated: Contact) {
        val index = contacts.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            contacts[index] = updated
        }
    }

    fun deleteContact(id: Long) {
        contacts.removeAll { it.id == id }
    }
}
