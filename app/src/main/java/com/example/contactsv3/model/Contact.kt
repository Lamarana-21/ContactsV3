package com.example.contactsv3.model

data class Contact(
    val id: Long,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String,
    var photoUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
)
