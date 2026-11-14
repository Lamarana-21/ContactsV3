package com.example.contactsv3.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.contactsv3.R
import com.example.contactsv3.model.Contact
import com.example.contactsv3.viewmodel.ContactViewModel
import java.io.File
import java.io.FileOutputStream

class DetailContactFragment : Fragment(R.layout.fragment_detail_contact) {

    private val viewModel: ContactViewModel by activityViewModels()
    private var contactId: Long = -1L
    private var currentContact = Contact(0, "", "", "", "")
    private var photoUriString: String? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoUriString = it.toString()
            try {
                view?.findViewById<ImageView>(R.id.imgDetailPhoto)?.setImageURI(it)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur d'affichage de l'image", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(requireContext(), "Aucune image sélectionnée", Toast.LENGTH_SHORT).show()
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            view?.findViewById<ImageView>(R.id.imgDetailPhoto)?.setImageBitmap(it)
            try {
                val file = File(requireContext().filesDir, "contact_photo_${System.currentTimeMillis()}.jpg")
                val stream = FileOutputStream(file)
                it.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                stream.close()
                photoUriString = file.absolutePath
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur lors de l'enregistrement de la photo", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(requireContext(), "Prise de photo annulée", Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Caméra non disponible", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Permission de la caméra refusée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactId = arguments?.getLong("contact_id", -1L) ?: -1L
        val foundContact = viewModel.getContactById(contactId)
        if (foundContact != null) {
            currentContact = foundContact
        }

        val imgPhoto = view.findViewById<ImageView>(R.id.imgDetailPhoto)
        val etNom = view.findViewById<EditText>(R.id.etDetailNom)
        val etPrenom = view.findViewById<EditText>(R.id.etDetailPrenom)
        val etPhone = view.findViewById<EditText>(R.id.etDetailPhone)
        val etEmail = view.findViewById<EditText>(R.id.etDetailEmail)
        val etLatitude = view.findViewById<EditText>(R.id.etDetailLatitude)
        val etLongitude = view.findViewById<EditText>(R.id.etDetailLongitude)

        val btnGallery = view.findViewById<Button>(R.id.btnGallery)
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnCall = view.findViewById<Button>(R.id.btnCall)
        val btnEmail = view.findViewById<Button>(R.id.btnEmail)
        val btnShowMap = view.findViewById<Button>(R.id.btnShowMap)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        currentContact.let { contact ->
            etNom.setText(contact.nom)
            etPrenom.setText(contact.prenom)
            etPhone.setText(contact.telephone)
            etEmail.setText(contact.email)
            contact.latitude?.let { etLatitude.setText(it.toString()) }
            contact.longitude?.let { etLongitude.setText(it.toString()) }
            photoUriString = contact.photoUri
            if (!photoUriString.isNullOrEmpty()) {
                try {
                    val uri = Uri.parse(photoUriString)
                    if (uri.scheme == null) {
                        imgPhoto.setImageURI(Uri.fromFile(File(photoUriString!!)))
                    } else {
                        imgPhoto.setImageURI(uri)
                    }
                } catch (e: Exception) {
                    imgPhoto.setImageResource(R.mipmap.ic_launcher)
                }
            }
        }

        btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    cameraLauncher.launch(null)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Caméra non disponible", Toast.LENGTH_SHORT).show()
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        btnCall.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Impossible de lancer l'appel", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Numéro de téléphone vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Aucune application de messagerie trouvée", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Adresse e-mail vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnShowMap.setOnClickListener {
            val mapFragment = CarteContactFragment().apply {
                arguments = Bundle().apply {
                    putLong("contact_id", contactId)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, mapFragment)
                .addToBackStack(null)
                .commit()
        }

        btnSave.setOnClickListener {
            val nom = etNom.text.toString().trim()
            val prenom = etPrenom.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val latStr = etLatitude.text.toString().trim()
            val lngStr = etLongitude.text.toString().trim()

            if (nom.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Le nom et le téléphone sont obligatoires.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var latitude: Double? = null
            if (latStr.isNotEmpty()) {
                val lat = latStr.toDoubleOrNull()
                if (lat == null || lat < -90.0 || lat > 90.0) {
                    Toast.makeText(requireContext(), "Latitude invalide (doit être entre -90 et 90).", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                latitude = lat
            }

            var longitude: Double? = null
            if (lngStr.isNotEmpty()) {
                val lng = lngStr.toDoubleOrNull()
                if (lng == null || lng < -180.0 || lng > 180.0) {
                    Toast.makeText(requireContext(), "Longitude invalide (doit être entre -180 et 180).", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                longitude = lng
            }

            currentContact.let { contact ->
                contact.nom = nom
                contact.prenom = prenom
                contact.telephone = phone
                contact.email = email
                contact.photoUri = photoUriString
                contact.latitude = latitude
                contact.longitude = longitude
                viewModel.updateContact(contact)
            }

            Toast.makeText(requireContext(), "Contact enregistré avec succès.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
